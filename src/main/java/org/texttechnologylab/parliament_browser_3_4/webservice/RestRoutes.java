package org.texttechnologylab.parliament_browser_3_4.webservice;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.util.JSON;
import freemarker.template.Configuration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.texttechnologylab.parliament_browser_3_4.Main;
import org.texttechnologylab.parliament_browser_3_4.data.*;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Comment_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Factory_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Speaker_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Speech_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Parliament_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.database.MongoDBHandler;
import org.texttechnologylab.parliament_browser_3_4.latex.LatexBuilder;
import org.texttechnologylab.parliament_browser_3_4.latex.template.ProtocolLatexTemplate;
import org.texttechnologylab.parliament_browser_3_4.user_data.User;
import spark.ModelAndView;
import spark.Response;
import spark.Session;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * REST Routes Class
 * @author Simon Schuett, Stanley Mathew, Kenan Khauto
 */
public class RestRoutes {

    private final Configuration config;
    private final Factory_MongoDB_Impl parliament;
    protected static MongoDBHandler mongoDB = null;

    private static Session session = null;

    /** speakerSpeechCount Cache */
    private static JSONArray speakerSpeechCount = null;
    /** speechCount Cache */
    private static long speechCount = -1L;
    /** commentCount Cache */
    private static long commentCount = -1L;

    /** Speaker Picture Url cache */
    private static Map<String, String> picUrls = null;

    /** Additional Speaker Info cache */
    private static HashMap<String, HashMap<String, String>> speakerInfos = null;
    private static HashMap<String, Set<String>> speakerSpeeches = null;

    /** tokenDistribution Cache */
    protected static JSONArray tokenCache = null;

    /** posDistribution Cache */
    protected static JSONArray posCache = null;

    /** sentimentDistribution Cache */
    protected static JSONArray sentimentCache = null;

    /** namedEntityDistribution Cache */
    protected static JSONArray namedEntityCachePER = null;

    /** namedEntityDistribution Cache */
    protected static JSONArray namedEntityCacheLOC = null;

    /** namedEntityDistribution Cache */
    protected static JSONArray namedEntityCacheORG = null;


    /** speakerDistribution Cache */
    protected static JSONArray speakerCache = null;

    /** xml parse data Cache */
    protected static Parliament parliamentXML = null;


    /**
     * Constructor for RestRoutes
     * @param config     FreeMarker Config
     * @param parliament Parliament Corpus for data access
     * @author Simon Schuett
     */
    public RestRoutes(Configuration config, Factory_MongoDB_Impl parliament, MongoDBHandler mongoDB) {
        this.config = config;
        this.parliament = parliament;
        this.mongoDB = mongoDB;
    }

    /**
     * REST-Routes for Freemarker-Template view requests. Usually return ModelAndView object and uses FreeMarkerEngine.
     * @author Stanley Mathew, Simon Schuett
     */
    public void createSiteRoutes() {
        Spark.get("/", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Parliament Browser");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/deputies", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Deputies");
            attributes.put("parliament", parliament);

            int page = Integer.parseInt(request.queryParams().contains("page") ? request.queryParams("page") : "0");
            attributes.put("page", page);
            attributes.put("pageCount", parliament.getSpeakers().size());

            HashMap<String, Speaker> speakers = new HashMap<>();
            parliament.getSpeakers().entrySet().stream().skip(100L * page).limit(100).forEach(speakerEntry -> {
                speakers.put(speakerEntry.getKey(), speakerEntry.getValue());
            });
            attributes.put("speakers", speakers);

            if (picUrls == null) {
                picUrls = RouteFunctions.getPictureUrls();
            }
            attributes.put("picUrls", picUrls);

            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            return new ModelAndView(attributes, "deputies.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/deputy", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            String id = request.queryParams().contains("id") ? request.queryParams("id") : "";
            attributes.put("title", id);
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            if (picUrls == null) {
                picUrls = RouteFunctions.getPictureUrls();
            }
            attributes.put("picUrls", picUrls);

            if (speakerInfos == null || speakerSpeeches == null) {
                speakerInfos = new HashMap<>();
                speakerSpeeches = new HashMap<>();
                AggregateIterable it = RouteFunctions.getSpeakerInfos();
                it.forEach((Consumer<Document>) doc -> {
                    HashMap<String, String> speakerMap = new HashMap<>();
                    speakerMap.put("birthdate", doc.getString("Birth Date"));
                    speakerMap.put("birthplace", doc.getString("Birth Place"));
                    speakerMap.put("birthland", doc.getString("Birth Land"));
                    speakerMap.put("deathdate", doc.getString("Death Date"));
                    speakerMap.put("sex", doc.getString("Sex"));
                    speakerMap.put("familystatus", doc.getString("Family Status"));
                    speakerMap.put("profession", doc.getString("Profession"));
                    speakerMap.put("vita", doc.getString("Vita"));
                    speakerInfos.put(doc.getString("_id"), speakerMap);

                    Set<String> speeches = new HashSet<>();
                    speeches.addAll(doc.getList("speeches", String.class));

                    speakerSpeeches.put(doc.getString("_id"), speeches);
                });
            }
            attributes.put("speakerInfos", speakerInfos.get(id));
            attributes.put("speakerSpeeches", speakerSpeeches.get(id));

            Speaker speaker = parliament.getSpeakerByIDAg(id);
            attributes.put("deputy", speaker);

            return new ModelAndView(attributes, "singleView/deputy.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/parties", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Parties");
            attributes.put("parliament", parliament);
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            return new ModelAndView(attributes, "parties.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/party", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            String id = request.queryParams().contains("id") ? request.queryParams("id") : "";
            attributes.put("title", id);

            Party party = parliament.getParties().get(id);
            attributes.put("party", party);

            return new ModelAndView(attributes, "singleView/party.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/factions", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Factions");
            attributes.put("parliament", parliament);
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            return new ModelAndView(attributes, "factions.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/faction", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            String id = request.queryParams().contains("id") ? request.queryParams("id") : "";
            attributes.put("title", id);

            Faction faction = parliament.getFactions().get(id);
            attributes.put("faction", faction);
            System.out.println(faction);

            return new ModelAndView(attributes, "singleView/faction.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/comment", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            String id = request.queryParams().contains("id") ? request.queryParams("id") : "";
            attributes.put("title", id);

            Document query = new Document("_id", id);

            mongoDB.getObjects(query, "nlpComment").forEachRemaining(document -> {
                Comment comment = new Comment_MongoDB_Impl(document);
                attributes.put("comment", comment);
            });

            return new ModelAndView(attributes, "singleView/comment.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/speech", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            String id = request.queryParams().contains("id") ? request.queryParams("id") : "";
            attributes.put("title", id);

            // Speech speech = parliament.getSpeeches().get(id);
            Speech speech = parliament.getSpeechByIDAg(id);
            attributes.put("speech", speech);

            return new ModelAndView(attributes, "singleView/speech.ftl");
        }, new FreeMarkerEngine(config));

        /**
         * Search
         */
        Spark.get("/search", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Search/Query");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            return new ModelAndView(attributes, "singleSearch/search.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/deputyResults", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Deputy Search");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            Document query = new Document();
            if (request.queryParams().contains("lastname"))
                query.put("Last Name", request.queryParams("lastname"));
            if (request.queryParams().contains("firstname"))
                query.put("First Name", request.queryParams("firstname"));
            if (request.queryParams().contains("id"))
                query.put("_id", request.queryParams("id"));

            List<Speaker> result = new ArrayList<>();
            mongoDB.getObjects(query, "Speaker").forEachRemaining(document -> {
                Speaker speaker = new Speaker_MongoDB_Impl(document);
                result.add(speaker);
            });
            attributes.put("deputies", result);

            return new ModelAndView(attributes, "singleSearch/deputyResults.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/speechResults", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Speech Search");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            Document query = new Document();
            if (request.queryParams().contains("term"))
                query.append("$text", new Document().append("$search", request.queryParams("term")));
            if (request.queryParams().contains("id"))
                query.append("_id", request.queryParams("id"));

            // Create index if not present
            IndexOptions options = new IndexOptions().defaultLanguage("de").name("FullTextSearchSpeech");
            mongoDB.getDatabase().getCollection("nlpSpeech").createIndex(Indexes.text("Text"), options);

            List<Speech> result = new ArrayList<>();
            mongoDB.getObjects(query, "nlpSpeech").forEachRemaining(document -> {
                Speech speech = new Speech_MongoDB_Impl(document);
                result.add(speech);
            });
            attributes.put("speeches", result);

            return new ModelAndView(attributes, "singleSearch/speechResults.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/commentResults", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Comment Search");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            Document query = new Document();
            if (request.queryParams().contains("term"))
                query.append("$text", new Document().append("$search", request.queryParams("term")));
            if (request.queryParams().contains("id"))
                query.append("_id", request.queryParams("id"));

            // Create index if not present
            IndexOptions options = new IndexOptions().defaultLanguage("de").name("FullTextSearchComment");
            mongoDB.getDatabase().getCollection("nlpComment").createIndex(Indexes.text("content"), options);

            List<Comment> result = new ArrayList<>();
            mongoDB.getObjects(query, "nlpComment").forEachRemaining(document -> {
                Comment comment = new Comment_MongoDB_Impl(document);
                result.add(comment);
            });
            attributes.put("comments", result);

            return new ModelAndView(attributes, "singleSearch/commentResults.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/searchGeneral", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            String term = request.queryParams().contains("term") ? request.queryParams("term") : "";
            attributes.put("term", term);

            return new ModelAndView(attributes, "singleSearch/searchGeneral.ftl");
        }, new FreeMarkerEngine(config));

        /**
         * Graphs Visualisations
         */
        Spark.get("/graphs", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "NLP Analysis Graphs");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            return new ModelAndView(attributes, "graphs.ftl");
        }, new FreeMarkerEngine(config));

        /**
         * Fulltext Speech Analysis Routes
         */
        Spark.get("/fulltextSpeechSite", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Fulltext Speech Analysis");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            attributes.put("parliament", parliament);

            return new ModelAndView(attributes, "fulltextSpeech/fulltextSpeechSite.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/fulltextSpeech", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            String speechId = request.queryParams().contains("speech") ? request.queryParams("speech") : "";
            attributes.put("title", speechId);

            if (picUrls == null) {
                picUrls = RouteFunctions.getPictureUrls();
            }
            attributes.put("picUrls", picUrls);

            HashMap<String, String> infos = new HashMap<>();
            ArrayList<HashMap<String, String>> sentences = new ArrayList<>();
            HashSet<String> persons = new HashSet<>();
            HashSet<String> locations = new HashSet<>();
            HashSet<String> organisations = new HashSet<>();

            AggregateIterable it = RouteFunctions.getFulltextSpeechAnalysis(speechId);
            it.forEach((Consumer<Document>) doc -> {
                infos.put("id", doc.getString("_id"));
                infos.put("SpeakerName", doc.getString("SpeakerName"));
                infos.put("Party", doc.getString("Party"));
                infos.put("Faction", doc.getString("Faction"));
                infos.put("SpeakerID", doc.getString("SpeakerID"));

                for (Document sentenceDoc : doc.getList("Sentiment", Document.class)) {
                    HashMap<String, String> sentence = new HashMap<>();
                    sentence.put("text", sentenceDoc.getString("text"));
                    sentence.put("sentiment", sentenceDoc.getDouble("sentiment").toString());
                    sentences.add(sentence);
                }
                persons.addAll(doc.getList("Persons", String.class));
                locations.addAll(doc.getList("Locations", String.class));
                organisations.addAll(doc.getList("Organisations", String.class));
            });

            attributes.put("infos", infos);
            attributes.put("sentences", sentences);
            attributes.put("persons", persons);
            attributes.put("locations", locations);
            attributes.put("organisations", organisations);

            return new ModelAndView(attributes, "fulltextSpeech/fulltextSpeech.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/dayTopicList", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            String protocolId = request.queryParams().contains("protocol") ? request.queryParams("protocol") : "";
            attributes.put("title", protocolId);

            Protocol protocol = parliament.getProtocolByIDAg(protocolId);
            attributes.put("protocol", protocol);

            return new ModelAndView(attributes, "fulltextSpeech/dayTopicList.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/speechList", "application/json", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            String dayTopicId = request.queryParams().contains("dayTopic") ? request.queryParams("dayTopic") : "";
            attributes.put("title", dayTopicId);

            DayTopic dayTopic = parliament.getDayTopicByIDAg(dayTopicId);
            attributes.put("dayTopic", dayTopic);

            Set<Map<String, String>> speechSet = new HashSet<>();
            for (Speech speech : dayTopic.getSpeechList()) {
                HashMap<String, String> speechMap = new HashMap<>();
                speechMap.put("id", speech.getID());
                if (speech.getSpeaker() == null) {
                    speechMap.put("speaker", "");
                } else {
                    speechMap.put("speaker", speech.getSpeaker().getName());
                }
                speechSet.add(speechMap);
            }

            return new JSONArray(speechSet);
        });

        Spark.get("/speechComments", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            String id = request.queryParams().contains("id") ? request.queryParams("id") : "";
            attributes.put("title", id);

            // Speech speech = parliament.getSpeeches().get(id);
            Speech speech = parliament.getSpeechByIDAg(id);
            attributes.put("speech", speech);

            return new ModelAndView(attributes, "fulltextSpeech/speechComments.ftl");
        }, new FreeMarkerEngine(config));

        /**
         * Network Routes
         */
        Spark.get("/commentNetwork", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Comment Network");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            return new ModelAndView(attributes, "networks/commentNetwork.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/speechTopicNetwork", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Speech Topic Network");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            return new ModelAndView(attributes, "networks/speechTopicNetwork.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/speechSentimentNetwork", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Speech Sentiment and Topic Network");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            return new ModelAndView(attributes, "networks/speechSentTopicNetwork.ftl");
        }, new FreeMarkerEngine(config));

        /**
         * PDF Export Routes
         */
        Spark.get("/latexExport", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "PDF Export");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            attributes.put("parliament", parliament);

            return new ModelAndView(attributes, "latexExport.ftl");
        }, new FreeMarkerEngine(config));

        Spark.get("/protocolPdf", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Protocol PDF");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            String protocolId = request.queryParams().contains("protocol") ? request.queryParams("protocol") : "";
            attributes.put("protocolId", protocolId);
            Protocol protocol = parliament.getProtocolByIDAg(protocolId);

            System.out.println("PDF " + protocolId + " Start");
            ProtocolLatexTemplate protocolTemplate = new ProtocolLatexTemplate(protocol);
            LatexBuilder texBuild = new LatexBuilder();
            System.out.println("Protocol " + protocolId + " Init Done");

            texBuild.createPDF(protocolTemplate, "templates/public/pdf", protocolId, "pdflatex");
            System.out.println("PDF " + protocolId + " Gen Done");

            return new ModelAndView(attributes, "protocolPdf.ftl");
        }, new FreeMarkerEngine(config));

    }


    /**
     * REST-Routes for pure data requests. Usually return JSONArrays or other common data formats.
     * @author Stanley Mathew, Simon Schuett
     */
    public void createDataRoutes() {
        // TOP-Speakers
        Spark.get("/stats/top-speakers", "application/json", (request, response) -> {
            if (speakerSpeechCount == null) {
                JSONArray result = new JSONArray();

                AggregateIterable it = RouteFunctions.speakerSpeechCount("", null, 3);

                it.forEach((Consumer<Document>) document -> result.put(document));
                speakerSpeechCount = result;

            }
            return speakerSpeechCount;
        });
        // Total Speech Count of corpus
        Spark.get("/stats/speechCount", "text/plain", (request, response) -> {
            if (speechCount == -1L) {
                Document query = new Document();
                speechCount = mongoDB.countDocuments(query, "nlpSpeech");

            }
            return speechCount;
        });
        // Total Comment Count of corpus
        Spark.get("/stats/commentCount", "text/plain", (request, response) -> {
            if (commentCount == -1L) {
                Document query = new Document();
                commentCount = mongoDB.countDocuments(query, "nlpComment");
            }
            return commentCount;
        });


        get("/tokenDistribution", "application/json", (request, response) -> {
            return RouteFunctions.getTokenDistribution(
                    new SimpleDateFormat("dd.MM.yyyy").parse("01.01.1970"),
                    new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2100"));
        });

        post("/graphToken", "application/json", (request, response) -> {

            Document timeFilter = Document.parse(request.body());

            Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(timeFilter.getString("startDate"));
            Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(timeFilter.getString("endDate"));

            return RouteFunctions.getTokenDistribution(startDate, endDate);
        });


        get("/posDistribution", "application/json", (request, response) -> {
            return RouteFunctions.getPosDistribution();
        });

        get("/sentimentDistribution", "application/json", (request, response) -> {
            return RouteFunctions.getSentimentDistribution();
        });

        get("/namedEntityDistributionPER", "application/json", (request, response) -> {
            return RouteFunctions.getNamedEntityDistributionPER();
        });

        get("/namedEntityDistributionLOC", "application/json", (request, response) -> {
            return RouteFunctions.getNamedEntityDistributionLOC();
        });

        get("/namedEntityDistributionORG", "application/json", (request, response) -> {
            return RouteFunctions.getNamedEntityDistributionORG();
        });


        get("/speakerDistribution", "application/json", (request, response) -> {
            return RouteFunctions.getSpeakerDistribution();
        });

        get("/reimportToMongoAndNlp", (request, response) -> {
            System.out.println("Parsing XML ...");
            mongoDB.protocolToMongo(Main.class.getClassLoader().getResource("MDB_STAMMDATEN.XML").getPath().replace("/MDB_STAMMDATEN.XML", ""));
            System.out.println("Finished XML Parsing!");

            System.out.println("Starting NLP Analysis");
            mongoDB.speechAnalysis();
            System.out.println("NLP Analysis Done.");

            return new JSONArray().put("ok");
        });

    }

    /**
     * Rest Routes for the Network Graph Data
     * @author Simon Schuett
     */
    public void createNetworkGraphRoutes() {

        get("/commentNetworkGraph", "application/json", ((request, response) -> {
            int sampleSize = request.queryParams().contains("sample") ? Integer.parseInt(request.queryParams("sample")) : 1000;

            return RouteFunctions.getCommentNetwork(sampleSize);
        }));

        get("/speechTopicGraph", "application/json", ((request, response) -> {
            int sampleSize = request.queryParams().contains("sample") ? Integer.parseInt(request.queryParams("sample")) : 1000;
            double scoreThreshold = request.queryParams().contains("minScore") ? Double.parseDouble(request.queryParams("minScore")) : 0.1;

            return RouteFunctions.getSpeechTopicNetwork(sampleSize, scoreThreshold);
        }));

        get("/speechSentTopicGraph", "application/json", ((request, response) -> {
            int sampleSize = request.queryParams().contains("sample") ? Integer.parseInt(request.queryParams("sample")) : 1000;
            double scoreThreshold = request.queryParams().contains("minScore") ? Double.parseDouble(request.queryParams("minScore")) : 0.1;

            return RouteFunctions.getSpeechSentTopicNetwork(sampleSize, scoreThreshold);
        }));

    }

    /**
     * Rest Routes for login, logout, create account
     * @author Kenan Khauto
     */
    public void createLoginRoutes() {

        Spark.before("/login", "text/html", (request, response) -> {
            if (session != null) {
                response.redirect("/");
            }
        });

        Spark.get("/login", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Login");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            return new ModelAndView(attributes, "login.ftl");
        }, new FreeMarkerEngine(config));

        Spark.post("/login", (req, res) -> {

            Document loggedUser = Document.parse(req.body());

            String username = loggedUser.getString("username");
            String password = loggedUser.getString("password");

            if (mongoDB.getDocumentById(username, "user") != null) {
                if (mongoDB.getDocumentById(username, "user").get("password").equals(password)) {
                    session = req.session(true);
                    session.attribute("username", username);
                    session.attribute("password", password);
                    session.attribute("role", mongoDB.getDocumentById(username, "user").getString("role"));

                    res.redirect("/");
                }
                return JSON.parse("{'message':'Password Wrong!'}");
            } else {
                return JSON.parse("{'message':'username does not exist!'}");
            }
        });

        Spark.get("/create-account", "text/html", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Create Account");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            return new ModelAndView(attributes, "createAccount.ftl");
        }, new FreeMarkerEngine(config));

        Spark.post("/create-account", (req, res) -> {

            //this still needs modifications

            Document account = Document.parse(req.body());

            try {
                account.append("_id", account.getString("username"));
                account.append("role", "user");
                mongoDB.insert("user", account);
                return JSON.parse("{'message':'Account created successfully'}");
            } catch (Exception e) {
                return JSON.parse("{'message':'username already exists!'}");
            }

        });

        get("/logout", (req, res) -> {
            res.type("text/html");
            if (req.session() != null) {
                req.session().invalidate();
                session = null;
            }
            res.redirect("/login");
            return null;
        });

        before("/admin-dashboard", ((request, response) -> {

            if (session == null) {
                // redirect to the login page
                response.redirect("/login");
            } else {
                if (request.session().attribute("role") != null && !request.session().attribute("role").equals("admin")) {
                    response.redirect("/");
                }
            }

        }));

        get("/admin-dashboard", (request, response) -> {
            response.type("text/html");

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", "Admin Dashboard");
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            attributes.put("users", mongoDB.getUsers());

            return new ModelAndView(attributes, "adminDashboard.ftl");
        }, new FreeMarkerEngine(config));

        post("/admin-dashboard/delete-user", (request, response) -> {
            response.type("text/html");

            mongoDB.deleteUser(request.body());
            return getModelAndView(response);
        }, new FreeMarkerEngine(config));

        post("/admin-dashboard/add-user", (request, response) -> {
            response.type("text/html");

            Document account = Document.parse(request.body());
            account.append("_id", account.getString("username"));

            try {
                mongoDB.insert("user", account);
                response.redirect("/admin-dashboard");
                return JSON.parse("{'message':'Account created successfully'}");
            } catch (Exception e) {
                return JSON.parse("{'message':'username already exists!'}");
            }

        });

        post("/admin-dashboard/promote-user", (request, response) -> {

            mongoDB.updateUser(request.body(), "role", "admin");

            return getModelAndView(response);
        }, new FreeMarkerEngine(config));

        post("/admin-dashboard/demote-user", (request, response) -> {

            mongoDB.updateUser(request.body(), "role", "user");

            return getModelAndView(response);
        }, new FreeMarkerEngine(config));

        get("/profile", (request, response) -> {

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            attributes.put("users", mongoDB.getUsers());

            Document userDoc = mongoDB.getUserById(session.attribute("username"));
            User user = new User(userDoc);

            attributes.put("user", user);


            return new ModelAndView(attributes, "profile.ftl");
        }, new FreeMarkerEngine(config));

        before("/profile", ((request, response) -> {

            if (session == null) {
                // redirect to the login page
                response.redirect("/login");
            }

        }));

        post("/profile/edit-user", (request, response) -> {

            Document account = Document.parse(request.body());
            account.append("_id", account.getString("username"));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);
            attributes.put("users", mongoDB.getUsers());

            Document userDoc = mongoDB.getUserById(session.attribute("username"));
            User user = new User(userDoc);

            attributes.put("user", user);

            account.append("role", user.getRole().getRoleName());

            session.attribute("username", account.getString("username"));
            session.attribute("password", account.getString("password"));

            mongoDB.deleteUser(user.getUsername());
            mongoDB.insert("user", account);


            return new ModelAndView(attributes, "profile.ftl");
        }, new FreeMarkerEngine(config));

        get("/load-parliament", (request, response) -> {

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("loggedIn", session != null && session.attribute("username") != null);

            attributes.put("title", "Raw Parliament Data From XML");

            if (parliamentXML == null) {
                System.out.println("Parsing ...");
                parliamentXML = new Parliament_File_Impl(Main.class.getClassLoader().getResource("MDB_STAMMDATEN.XML").getPath().replace("/MDB_STAMMDATEN.XML", ""));
                System.out.println("Finished!");
            }

            return new ModelAndView(attributes, "parliamentXML.ftl");
        }, new FreeMarkerEngine(config));

        get("/load-parliament/getPartysXML", (request, response) -> {

            if (parliamentXML != null) {
                List<String> partyNames = parliamentXML.getFactory().getParties()
                        .stream()
                        .map(Party::getName)
                        .collect(Collectors.toList());


                response.type("text/plain");
                response.header("Content-Encoding", "UTF-8");
                response.header("Access-Control-Allow-Origin", "*");
                return String.join("\n", partyNames);
            } else {
                response.status(500);
                return "Error: parliamentXML is null";
            }
        });


        get("/load-parliament/getSpeakersXML", (request, response) -> {

            if (parliamentXML != null) {
                List<String> speakers = parliamentXML.getFactory().getSpeakers()
                        .stream()
                        .map(speaker -> speaker.getName() + ", Role: " + speaker.getRole() + ", Party: " + speaker.getParty()
                                + ", Faction: " + speaker.getFaction())
                        .collect(Collectors.toList());


                response.type("text/plain");
                response.header("Content-Encoding", "UTF-8");
                response.header("Access-Control-Allow-Origin", "*");
                return String.join("\n", speakers);
            } else {
                response.status(500);
                return "Error: parliamentXML is null";
            }
        });

        get("/load-parliament/getProtocolsXML", (request, response) -> {

            if (parliamentXML != null) {
                List<String> protocols = parliamentXML.getFactory().getProtocols()
                        .stream()
                        .map(x -> x.getTitle() + ", " + x.getPlace() + ", " + x.getDateFormatted() + ", ::WP:: " + x.getLegislaturePeriod())
                        .collect(Collectors.toList());

                response.type("text/plain");
                response.header("Content-Encoding", "UTF-8");
                response.header("Access-Control-Allow-Origin", "*");
                return String.join("\n", protocols);
            } else {
                response.status(500);
                return "Error: parliamentXML is null";
            }
        });

        get("/load-parliament/getFactionsXML", (request, response) -> {

            if (parliamentXML != null) {
                List<String> factions = parliamentXML.getFactory().getFactions()
                        .stream()
                        .map(Faction::getName)
                        .collect(Collectors.toList());


                response.type("text/plain");
                response.header("Content-Encoding", "UTF-8");
                response.header("Access-Control-Allow-Origin", "*");
                return String.join("\n", factions);
            } else {
                response.status(500);
                return "Error: parliamentXML is null";
            }
        });

    }

    @NotNull
    private ModelAndView getModelAndView(Response response) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", "Admin Dashboard");
        attributes.put("loggedIn", session != null && session.attribute("username") != null);
        attributes.put("users", mongoDB.getUsers());

        response.redirect("/admin-dashboard");
        return new ModelAndView(attributes, "adminDashboard.ftl");
    }

}
