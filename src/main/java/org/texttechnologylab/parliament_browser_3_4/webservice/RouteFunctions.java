package org.texttechnologylab.parliament_browser_3_4.webservice;


import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import freemarker.template.Configuration;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.parliament_browser_3_4.Main;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Factory_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.database.MongoDBHandler;
import org.texttechnologylab.parliament_browser_3_4.helper.DDCLoader;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;


/**
 * Route - Functions  Class
 * @author Stanley Mathew, Simon Schuett
 */
public class RouteFunctions extends RestRoutes {


    /**
     * Constructor for RestRoutes
     * @param config     FreeMarker Config
     * @param parliament Parliament Corpus for data access
     * @param mongoDB
     */
    public RouteFunctions(Configuration config, Factory_MongoDB_Impl parliament, MongoDBHandler mongoDB) {
        super(config, parliament, mongoDB);
    }

    /**
     * Stanley Mathew:   3.1.(a)
     * Helper Methode for Rest Route:   /tokenDistribution
     * @return JSONArray
     */
    public static JSONArray getTokenDistribution(Date startDate, Date endDate) {
        if (tokenCache == null) {
            JSONArray array = new JSONArray();

            List<Bson> pipeline = null;
            pipeline = Arrays.asList(
                    new Document()
                            .append("$match", new Document()
                                    .append("$and", Arrays.asList(
                                                    new Document()
                                                            .append("dateLong", new Document()
                                                                    .append("$gt", startDate.getTime())
                                                            ),
                                                    new Document()
                                                            .append("dateLong", new Document()
                                                                    .append("$lt", endDate.getTime())
                                                            )
                                            )
                                    )
                            ),
                    new Document().append("$unwind", "$Tokens"),
                    new Document().append("$group", new Document()
                            .append("_id", "$Tokens")
                            .append("count", new Document()
                                    .append("$sum", 1.0))),
                    new Document().append("$sort", new Document()
                            .append("count", -1.0)),
                    new Document().append("$limit", 20)
            );

            AggregateIterable<Document> aggIter = mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipeline);
            MongoCursor mc = aggIter.iterator();

            while (mc.hasNext()) {
                array.put(mc.next());
            }
            tokenCache = array;
        }
        return tokenCache;
    }

    /**
     * Helper Methode for Rest Route:   /posDistribution
     * @return JSONArray
     */
    public static JSONArray getPosDistribution() {
        if (posCache == null) {
            JSONArray array = new JSONArray();

            List<Bson> pipeline = Arrays.asList(

                    new Document().append("$unwind", "$POS"),
                    new Document().append("$group", new Document()
                            .append("_id", "$POS")
                            .append("count", new Document()
                                    .append("$sum", 1.0))),
                    new Document().append("$sort", new Document()
                            .append("count", -1.0)),
                    new Document().append("$limit", 20));

            AggregateIterable<Document> aggIter = mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipeline);
            MongoCursor mc = aggIter.iterator();
            while (mc.hasNext()) {
                array.put(mc.next());
            }
            posCache = array;
        }
        return posCache;
    }


    /**
     * Helper Methode for Rest Route:    /sentimentDistribution
     * @return JSONArray
     */
    public static JSONArray getSentimentDistribution() {
        if (sentimentCache == null) {
            JSONArray array = new JSONArray();

            List<Bson> pipelinePositive = Arrays.asList(
                    new Document().append("$match", new Document()   //match stage
                            .append("SentimentWhole", new Document()
                                    .append("$gte", 0.5))),
                    new Document().append("$count", "positive"));     // count stage

            AggregateIterable<Document> aggIterPositive = mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipelinePositive);
            MongoCursor mcPositive = aggIterPositive.iterator();

            while (mcPositive.hasNext()) {
                array.put(mcPositive.next());
            }
            List<Bson> pipelineNegative = Arrays.asList(
                    new Document()                                  // match
                            .append("$match", new Document()
                                    .append("SentimentWhole", new Document()
                                            .append("$lte", -0.5))),
                    new Document().append("$count", "negative"));    // count

            AggregateIterable<Document> aggIterNegative = mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipelineNegative);
            MongoCursor mcNegative = aggIterNegative.iterator();

            while (mcNegative.hasNext()) {
                array.put(mcNegative.next());
            }

            List<Bson> pipelineNeutral = Arrays.asList(
                    new Document().append("$match", new Document()     // match with and operator
                            .append("$and", Arrays.asList(
                                    new Document().append("SentimentWhole", new Document()
                                            .append("$gt", -0.5)),
                                    new Document()
                                            .append("SentimentWhole", new Document()
                                                    .append("$lt", 0.5))))),

                    new Document().append("$count", "neutral"));      // count

            AggregateIterable<Document> aggIterNeutral = mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipelineNeutral);
            MongoCursor mcNeutral = aggIterNeutral.iterator();

            while (mcNeutral.hasNext()) {
                array.put(mcNeutral.next());
            }

            int number0, number1, number2;
            Document walla0 = (Document) array.get(0);

            number0 = (int) walla0.get("positive");
            Document home0 = new Document();
            home0.put("_id", "positive");
            home0.put("count", number0);


            Document walla1 = (Document) array.get(1);

            number1 = (int) walla1.get("negative");
            Document home1 = new Document();
            home1.put("_id", "negative");
            home1.put("count", number1);


            Document walla2 = (Document) array.get(2);

            number2 = (int) walla2.get("neutral");
            Document home2 = new Document();
            home2.put("_id", "neutral");
            home2.put("count", number2);


            JSONArray arrayNew = new JSONArray();
            arrayNew.put(home0);
            arrayNew.put(home1);
            arrayNew.put(home2);

            sentimentCache = arrayNew;
        }
        return sentimentCache;
    }

    /**
     * Helper Methode for Rest Route:   /namedEntityDistributionPER
     * @return JSONArray
     */
    public static JSONArray getNamedEntityDistributionPER() {
        if (namedEntityCachePER == null) {
            //Map<String, JSONArray> jsonMap = new HashMap<>(0);

            JSONArray PER = helpJsonArray("Persons", "nlpSpeech");
            JSONArray LOC = helpJsonArray("Locations", "nlpSpeech");
            JSONArray ORG = helpJsonArray("Organisations", "nlpSpeech");

            JSONArray array = new JSONArray();

            /*Document aux1 = new Document();
            aux1.put("NETyp", "PER");
            for (int i = 0; i < PER.length(); i++) {
                Document help = (Document) PER.get(i);
                aux1.put("count", help.get("count"));
                aux1.put("_id", help.get("_id"));
                array.put(aux1);
            }
            Document aux2 = new Document();
            aux2.put("NETyp", "LOC");
            for (int i = 0; i < LOC.length(); i++) {
                Document help = (Document) LOC.get(i);
                aux2.put("count", help.get("count"));
                aux2.put("_id", help.get("_id"));
                array.put(aux2);
            }
            Document aux3 = new Document();
            aux3.put("NETyp", "ORG");
            for (int i = 0; i < ORG.length(); i++) {
                Document help = (Document) ORG.get(i);
                aux3.put("count", help.get("count"));
                aux3.put("_id", help.get("_id"));
             t   array.put(aux3);
            }*/

            namedEntityCachePER = PER;
        }
        return namedEntityCachePER;
    }


    /**
     * Helper Methode for Rest Route:   /namedEntityDistributionLOC
     * @return JSONArray
     */
    public static JSONArray getNamedEntityDistributionLOC() {
        if (namedEntityCacheLOC == null) {
            //Map<String, JSONArray> jsonMap = new HashMap<>(0);

            JSONArray PER = helpJsonArray("Persons", "nlpSpeech");
            JSONArray LOC = helpJsonArray("Locations", "nlpSpeech");
            JSONArray ORG = helpJsonArray("Organisations", "nlpSpeech");

            JSONArray array = new JSONArray();

            namedEntityCacheLOC = LOC;
        }
        return namedEntityCacheLOC;
    }

    /**
     * Helper Methode for Rest Route:   /namedEntityDistributionORG
     * @return JSONArray
     */
    public static JSONArray getNamedEntityDistributionORG() {
        if (namedEntityCacheORG == null) {
            //Map<String, JSONArray> jsonMap = new HashMap<>(0);

            JSONArray PER = helpJsonArray("Persons", "nlpSpeech");
            JSONArray LOC = helpJsonArray("Locations", "nlpSpeech");
            JSONArray ORG = helpJsonArray("Organisations", "nlpSpeech");

            JSONArray array = new JSONArray();

            namedEntityCacheORG = ORG;
        }
        return namedEntityCacheORG;
    }

    /**
     * Helper Methode for "getNamedEntityDistribution" above.
     * Mongo Request: $unwind entity, $group by entities, count, sort and limit
     * @return JSONArray
     */
    public static JSONArray helpJsonArray(String namedEnt, String collection) {
        JSONArray localArray = new JSONArray();
        List<Bson> query = Arrays.asList(
                Aggregates.unwind("$" + namedEnt),
                Aggregates.group("$" + namedEnt, sum("count", 1)),
                Aggregates.sort(Sorts.descending("count")),
                Aggregates.limit(20)
        );
        AggregateIterable<Document> it = mongoDB.getDatabase().getCollection(collection).aggregate(query);
        MongoCursor mc = it.iterator();
        while (mc.hasNext()) {
            localArray.put(mc.next());
        }
        return localArray;
    }

    /**
     * Helper Methode for Rest Route:  /speakerDistribution
     * @return JSONArray
     */
    public static JSONArray getSpeakerDistribution() {
        if (speakerCache == null) {

            JSONArray array = new JSONArray();

            List<Bson> pipeline = Arrays.asList(
                    new Document().append("$group", new Document()
                            .append("_id", "$SpeakerName")
                            .append("count", new Document()
                                    .append("$sum", 1.0))),
                    new Document().append("$sort", new Document()
                            .append("count", -1.0)),
                    new Document().append("$limit", 20));
            AggregateIterable<Document> aggIter = mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipeline);
            MongoCursor mc = aggIter.iterator();
            while (mc.hasNext()) {
                array.put(mc.next());
            }

            speakerCache = array;
        }
        return speakerCache;
    }

    /**
     * Returns the comment network graph.
     * @param sampleSize The amount of comments to consider for analysis
     * @return Graph G = (V, E), where V are the vertices and E are the Edges
     * @author Simon Schütt
     */
    public static JSONObject getCommentNetwork(int sampleSize) {

        List<Bson> pipeline = Arrays.asList(
                Aggregates.limit(sampleSize),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "speechID", "SpeakerID")
                )),
                Aggregates.lookup("nlpSpeech", "speechID", "_id", "speech"),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "SpeakerID"),
                        Projections.computed("speech", new Document().append("$first", "$speech"))
                )),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "SpeakerID"),
                        Projections.computed("speechSpeakerID", "$speech.SpeakerName"),
                        Projections.computed("sentiment", new Document().append("$first", "$speech.Sentiment"))
                )),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "SpeakerID", "speechSpeakerID"),
                        Projections.computed("sentiment", "$sentiment.sentiment")
                )),
                Aggregates.group(new Document().append("source", "$SpeakerID").append("target", "$speechSpeakerID"), avg("sentiment", "$sentiment"), sum("value", 1)),
                Aggregates.project(Projections.fields(
                        Projections.include("sentiment", "value"),
                        Projections.computed("source", "$_id.source"),
                        Projections.computed("target", "$_id.target"),
                        Projections.exclude("_id")
                ))
        );

        AggregateIterable aggregate = mongoDB.getDatabase().getCollection("nlpComment").aggregate(pipeline).allowDiskUse(true);

        JSONArray edges = new JSONArray();
        Set<Document> nodeSet = new HashSet<>();
        aggregate.forEach((Consumer<Document>) document -> {
            if (document.containsKey("sentiment") && document.get("sentiment") != null) {
                Document source = new Document().append("id", document.get("source"));
                Document target = new Document().append("id", document.get("target"));
                nodeSet.add(source);
                nodeSet.add(target);

                String type = "";
                if (document.getDouble("sentiment") == 0.0) {
                    type = "neutral";
                } else if (document.getDouble("sentiment") > 0.0) {
                    type = "positive";
                } else {
                    type = "negative";
                }
                document.append("type", type);

                edges.put(document);
            }
        });

        JSONArray nodes = new JSONArray(nodeSet);

        JSONObject graph = new JSONObject();
        graph.put("nodes", nodes);
        graph.put("links", edges);

        return graph;
    }

    /**
     * Returns the speech topic network graph.
     * @param sampleSize     The amount of comments to consider for analysis
     * @param scoreThreshold Any topic below this score gets filtered out (per speech)
     * @return Graph G = (V, E), where V are the vertices and E are the Edges
     * @author Simon Schütt
     */
    public static JSONObject getSpeechTopicNetwork(int sampleSize, double scoreThreshold) {

        List<Bson> pipeline = Arrays.asList(
                Aggregates.limit(sampleSize),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "SpeakerName"),
                        Projections.computed("DDCs", new Document().append("$map", new Document()
                                .append("input", new Document().append("$zip", new Document()
                                        .append("inputs", Arrays.asList("$ddcList", "$scoreList"))))
                                .append("as", "ddc")
                                .append("in", new Document().append("ddc", new Document().append("$arrayElemAt", Arrays.asList("$$ddc", 0)))
                                        .append("score", new Document().append("$arrayElemAt", Arrays.asList("$$ddc", 1)))))
                        ))),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "SpeakerName"),
                        Projections.computed("DDCs", new Document().append("$filter", new Document()
                                .append("input", "$DDCs")
                                .append("as", "ddc")
                                .append("cond", new Document().append("$gte", Arrays.asList("$$ddc.score", scoreThreshold))))
                        ))),
                Aggregates.unwind("$DDCs"),
                Aggregates.group(new Document().append("SpeakerName", "$SpeakerName").append("ddc", "$DDCs.ddc"), sum("score", "$DDCs.score")),
                Aggregates.group("$_id.SpeakerName", push("DDCs", new Document().append("ddc", "$_id.ddc").append("score", "$score")))

        );

        AggregateIterable aggregate = mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipeline).allowDiskUse(true);

        DDCLoader ddcLoader = new DDCLoader();
        ddcLoader.loadDDCfromCSV(new File(Main.class.getClassLoader().getResource("ddc3-names-de.csv").getPath()));

        JSONArray edges = new JSONArray();
        Set<Document> nodeSet = new HashSet<>();
        aggregate.forEach((Consumer<Document>) document -> {
            List<Document> ddcList = document.getList("DDCs", Document.class);
            String speaker = document.getString("_id");
            for (Document ddc : ddcList) {
                String topic = ddcLoader.getDDCs().get(Integer.parseInt(ddc.getString("ddc").substring(13)));
                nodeSet.add(new Document().append("id", topic).append("group", 2));
                edges.put(new Document().append("source", speaker).append("target", topic));
            }

            nodeSet.add(new Document().append("id", speaker).append("group", 0));

        });
        JSONArray nodes = new JSONArray(nodeSet);

        JSONObject graph = new JSONObject();
        graph.put("nodes", nodes);
        graph.put("links", edges);

        return graph;
    }

    /**
     * Returns the speech topic network graph.
     * @param sampleSize     The amount of comments to consider for analysis
     * @param scoreThreshold Any topic below this score gets filtered out (per speech)
     * @return Graph G = (V, E), where V are the vertices and E are the Edges
     * @author Simon Schütt
     */
    public static JSONObject getSpeechSentTopicNetwork(int sampleSize, double scoreThreshold) {

        List<Bson> pipeline = Arrays.asList(
                Aggregates.limit(sampleSize),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "SpeakerName", "SentimentWhole"),
                        Projections.computed("DDCs", new Document().append("$map", new Document()
                                .append("input", new Document().append("$zip", new Document()
                                        .append("inputs", Arrays.asList("$ddcList", "$scoreList"))))
                                .append("as", "ddc")
                                .append("in", new Document().append("ddc", new Document().append("$arrayElemAt", Arrays.asList("$$ddc", 0)))
                                        .append("score", new Document().append("$arrayElemAt", Arrays.asList("$$ddc", 1)))))
                        ))),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "SpeakerName", "SentimentWhole"),
                        Projections.computed("DDCs", new Document().append("$filter", new Document()
                                .append("input", "$DDCs")
                                .append("as", "ddc")
                                .append("cond", new Document().append("$gte", Arrays.asList("$$ddc.score", scoreThreshold))))
                        )))
        );

        AggregateIterable aggregate = mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipeline).allowDiskUse(true);

        DDCLoader ddcLoader = new DDCLoader();
        ddcLoader.loadDDCfromCSV(new File(Main.class.getClassLoader().getResource("ddc3-names-de.csv").getPath()));

        JSONArray edges = new JSONArray();
        Set<Document> nodeSet = new HashSet<>();
        aggregate.forEach((Consumer<Document>) document -> {
            List<Document> ddcList = document.getList("DDCs", Document.class);
            String speaker = document.getString("SpeakerName");
            String speechId = document.getString("_id");
            String speechName = speechId + " by " + speaker;
            nodeSet.add(new Document().append("id", speechName).append("group", 0)/*.append("x", 0).append("y", 0)*/);

            for (Document ddc : ddcList) {
                String topic = ddcLoader.getDDCs().get(Integer.parseInt(ddc.getString("ddc").substring(13)));
                nodeSet.add(new Document().append("id", topic).append("group", 2)/*.append("x", 1000).append("y", 0)*/);
                edges.put(new Document().append("source", speechName).append("target", topic));
            }

            double sentiment = document.getDouble("SentimentWhole");
            double roundedSentiment = Math.round(sentiment * 10.0) / 10.0;
            nodeSet.add(new Document().append("id", roundedSentiment).append("group", 3)/*.append("x", -1000).append("y", 0)*/);
            edges.put(new Document().append("source", speechName).append("target", roundedSentiment));
        });
        JSONArray nodes = new JSONArray(nodeSet);

        JSONObject graph = new JSONObject();
        graph.put("nodes", nodes);
        graph.put("links", edges);

        return graph;
    }

    /**
     * Aggregate to get speech count of each speaker.
     * @param filter    Party or faction name filter
     * @param intervall Date intervall [begin, end] for filtering
     * @param limit     Limits the amount of speakers in the result to the top n
     * @return Aggregate iterable of result
     * @author Simon Schuett
     */
    public static AggregateIterable speakerSpeechCount(String filter, List<Date> intervall, int limit) {
        // Date filter
        List<Bson> dateFilter = null;
        if (intervall != null) {
            dateFilter = Arrays.asList(
                    Aggregates.lookup("protocol", "agendaItem", "_id", "agi"),
                    new Document().append("$replaceRoot", new Document().append("newRoot", new Document()
                            .append("$mergeObjects", Arrays.asList(
                                            new Document().append("$arrayElemAt", Arrays.asList("$agi", 0.0)), "$$ROOT"
                                    )
                            )
                    )),
                    Aggregates.project(Projections.fields(
                            Projections.include("_id", "protocol", "speaker")
                    )),
                    Aggregates.lookup("protocol", "protocol", "_id", "prt"),
                    new Document().append("$replaceRoot", new Document().append("newRoot", new Document()
                            .append("$mergeObjects", Arrays.asList(
                                            new Document().append("$arrayElemAt", Arrays.asList("$prt", 0.0)), "$$ROOT"
                                    )
                            )
                    )),
                    Aggregates.project(Projections.fields(
                            Projections.include("_id", "speaker", "date")
                    )),
                    Aggregates.match(and(
                            gte("date", intervall.get(0)),
                            lte("date", intervall.get(1))
                    ))
            );
        }
        // Main mongo db aggregate
        List<Bson> query = new ArrayList<>();
        if (intervall != null) query.addAll(dateFilter);
        Aggregates.project(Projections.fields(
                Projections.include("_id", "SpeakerName", "Party", "Faction")
        ));
        query.add(Aggregates.group("$SpeakerName", sum("speechCount", 1)));
        // Faction/Party filter aggregate
        if (!filter.equals("")) {
            query.add(Aggregates.match(or(
                    Filters.eq("party", filter),
                    Filters.eq("faction", filter)
            )));
        }

        query.add(Aggregates.sort(descending("speechCount")));
        if (limit >= 0) query.add(Aggregates.limit(limit));

        return mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(query);
    }

    /**
     * Returns all relevant data for the fulltext speech analysis.
     * @param speechID ID of requested speech
     * @return Aggregate iterable of result
     * @author Simon Schütt
     */
    public static AggregateIterable getFulltextSpeechAnalysis(String speechID) {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(new Document("_id", speechID)),
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "SpeakerName", "SpeakerID", "Party", "Faction", "Sentiment", "Persons", "Locations", "Organisations")
                ))
        );

        return mongoDB.getDatabase().getCollection("nlpSpeech").aggregate(pipeline);
    }

    /**
     * Returns image links for speaker pictures.
     * @return Aggregate iterable of result
     * @author Simon Schütt
     */
    public static AggregateIterable getSpeakerPictures() {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "Name", "Picture Url", "Picture Info")
                ))
        );
        return mongoDB.getDatabase().getCollection("Speaker").aggregate(pipeline);
    }

    /**
     * Returns additional information about speaker.
     * @return Aggregate iterable of result
     * @author Simon Schütt
     */
    public static AggregateIterable getSpeakerInfos() {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.project(Projections.fields(
                        Projections.include("_id", "Name", "Birth Date", "Birth Place", "Birth Land", "Death Date", "Sex", "Family Status", "Profession", "Vita", "speeches")
                ))
        );
        return mongoDB.getDatabase().getCollection("Speaker").aggregate(pipeline);
    }

    public static Map<String, String> getPictureUrls() {
        Map<String, String> picUrls = new HashMap<>();
        AggregateIterable it = RouteFunctions.getSpeakerPictures();
        it.forEach((Consumer<Document>) document -> {
            picUrls.put(document.getString("_id"), document.getString("Picture Url"));
        });
        return picUrls;
    }

}
