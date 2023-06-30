package org.texttechnologylab.parliament_browser_3_4.database;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.parliament_browser_3_4.data.*;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.*;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Parliament_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.exceptions.InputException;
import org.texttechnologylab.parliament_browser_3_4.exceptions.PropertiesException;
import org.texttechnologylab.parliament_browser_3_4.helper.MembersDataHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static org.texttechnologylab.parliament_browser_3_4.nlp.NLPAnalyzeToMongo.nlpTool;

/**
 * MongoDBConnectionHandler
 * Handles all connections to the MongoDB
 * @author Maximilian Chen
 */


public class MongoDBHandler {
    private final MongoDatabase db;

    /**
     * Constructor for the MongoDBConnectionHandler
     * @param path The path to the properties file
     * @throws PropertiesException If the properties file is not found or in any other way invalid
     * @author Maximilian Chen
     */

    public MongoDBHandler(String path) throws PropertiesException {
        File config = new File(path);
        if (!config.exists()) {
            System.out.println("No properties file found at target location.");
            System.exit(0);
        }

        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(Paths.get(path)));
        } catch (IOException e) {
            System.out.println("Could not load properties file. It may already be open in another program?");
            e.printStackTrace();
        }
        String remote_host = props.getProperty("remote_host");
        String remote_database = props.getProperty("remote_database");
        String remote_user = props.getProperty("remote_user");
        String remote_password = props.getProperty("remote_password");
        String remote_port = props.getProperty("remote_port");
        String remote_collection = props.getProperty("remote_collection");
        if (remote_host == null || remote_database == null || remote_user == null || remote_password == null || remote_port == null || remote_collection == null) {
            throw new PropertiesException("Properties file has an error.");
        }

        int port = Integer.parseInt(remote_port);

        MongoCredential credential = MongoCredential.createScramSha1Credential(remote_user, remote_database, remote_password.toCharArray());
        List<ServerAddress> seeds = new ArrayList<>(0);
        seeds.add(new ServerAddress(remote_host, port));

        MongoClientOptions options = MongoClientOptions.builder().sslEnabled(false).build();
        MongoClient client = new MongoClient(seeds, credential, options);
        this.db = client.getDatabase(remote_database);
    }

    /**
     * Inserts a protocol into the database
     * @param path The path to the protocol
     * @author Maximilian Chen
     */
    public void protocolToMongo(String path) {
        Parliament p;
        System.out.println("Loading parliament.");
        try {
            p = new Parliament_File_Impl(path);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Loading protocols.");
        List<Protocol> protocols = new ArrayList<>(p.getFactory().getProtocols());
        System.out.println("Protocols loaded.");
        try {
            saveProtocol(protocols);
        } catch (InputException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts all speakers into the database
     * @param path The path to the stammdaten file
     * @author Maximilian Chen
     */
    public void speakerToMongo(String path) {
        MongoCollection<Document> SpeakerCollection = db.getCollection("Speaker");
        System.out.println("Loading protocols.");
        List<Document> protocols = getProtocolDocuments();
        System.out.println("Protocols loaded.");
        MembersDataHandler membersDataHandler = null;
        try {
            membersDataHandler = new MembersDataHandler(path);
        } catch (ParserConfigurationException | SAXException | IOException | ParseException | URISyntaxException e) {
            e.printStackTrace();
        }
        assert membersDataHandler != null;
        Set<Member> members = membersDataHandler.getMembers();
        Map<String, Document> documents = new HashMap<>();
        for (Member member : members) {
            Document memberDoc = member.toDocument();
            memberDoc.append("speeches", new ArrayList<String>());
            documents.put(member.getID(), memberDoc);
        }
        int i = 0;
        for (Document protocol : protocols) {
            for (Document dayTopic : protocol.getList("dayTopic", Document.class)) {
                for (Document speech : dayTopic.getList("speech", Document.class)) {
                    String speakerID = speech.getString("SpeakerID");
                    if (documents.containsKey(speakerID)) {
                        Document speaker = documents.get(speakerID);
                        List<String> speechList = speaker.getList("speeches", String.class);
                        speechList.add(speech.getString("_id"));
                        speaker.put("speeches", speechList);
                        documents.put(speakerID, speaker);
                        i++;
                        if (i % 1000 == 0) {
                            System.out.println("Processed " + i + " speeches.");
                        }
                    }
                }
            }
        }

        SpeakerCollection.insertMany(new ArrayList<>(documents.values()));
    }

    /**
     * Inserts all parties into the database
     * @param path The path to the stammdaten file
     * @author Maximilian Chen
     */
    public void partyToMongo(String path) {
        MongoCollection<Document> PartyCollection = db.getCollection("Party");
        Parliament p;
        System.out.println("Loading parliament.");
        try {
            p = new Parliament_File_Impl(path);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            return;
        }
        List<Party> partyList = new ArrayList<>(p.getFactory().getParties());
        saveParty(partyList);
    }

    /**
     * Inserts all factions into the database
     * @param path The path to the stammdaten file
     * @author Maximilian Chen
     */
    public void factionToMongo(String path) {
        Parliament p;
        System.out.println("Loading parliament.");
        try {
            p = new Parliament_File_Impl(path);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            return;
        }
        List<Faction> factionList = new ArrayList<>(p.getFactory().getFactions());
        System.out.println("Parliament loaded.");
        saveFaction(factionList);
    }

    /**
     * Save the speaker Map to the database
     * @param speakerMap the speaker Map to save
     * @author Maximilian Chen
     */
    public void saveSpeaker(Map<String, Speaker> speakerMap) {
        MongoCollection<Document> speakerCollection = this.db.getCollection("Speaker");
        List<Document> speakerList = new ArrayList<>();
        for (String id : speakerMap.keySet()) {
            Speaker s = speakerMap.get(id);
            speakerList.add(s.toDocument());
        }
        try {
            speakerCollection.insertMany(speakerList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Speakers already exist in database. Please use updateSpeaker() instead.");
            e.printStackTrace();
        }
    }

    /**
     * Save the speaker List to the database
     * @param SpeakerList the speaker Map to save
     * @author Maximilian Chen
     */
    public void saveSpeaker(List<Speaker> SpeakerList) {
        MongoCollection<Document> speakerCollection = this.db.getCollection("Speaker");
        List<Document> speakerList = new ArrayList<>();
        for (Speaker speaker : SpeakerList) {
            speakerList.add(speaker.toDocument());
        }
        try {
            speakerCollection.insertMany(speakerList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Speakers already exist in database. Please use updateSpeaker() instead.");
            e.printStackTrace();
        }
    }

    /**
     * Update the speaker Map in the database
     * @param a the speaker Map to update
     * @author Maximilian Chen
     */
    public void updateSpeaker(Map<String, Speaker> a) {
        MongoCollection<Document> speakerCollection = this.db.getCollection("Speaker");
        List<Document> speakerList = new ArrayList<>();
        for (String id : a.keySet()) {
            Speaker s = a.get(id);
            speakerList.add(s.toDocument());
        }
        try {
            speakerCollection.insertMany(speakerList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Speakers already exist in database. Updating instead.");
            e.printStackTrace();
            for (Document d : speakerList) {
                speakerCollection.replaceOne(new Document("_id", d.get("_id")), d);
            }
        }
    }

    /**
     * Get the speakers from the database
     * @param p Map of all parties with their name as key
     * @param f Map of all factions with their name as key
     * @return the list of speakers
     * @author Maximilian Chen
     */
    public List<Speaker> getSpeakerList(Map<String, Party> p, Map<String, Faction> f) {
        MongoCollection<Document> speakerCollection = this.db.getCollection("Speaker");
        List<Speaker> speakerList = new ArrayList<>();
        for (Document doc : speakerCollection.find()) {
            speakerList.add(new Speaker_MongoDB_Impl(doc, p, f));
        }
        return speakerList;
    }

    /**
     * Get the speakers from the database
     * @param p Map of all parties with their name as key
     * @param f Map of all factions with their name as key
     * @return the map of speakers
     * @author Maximilian Chen
     */
    public Map<String, Speaker> getSpeakerMap(Map<String, Party> p, Map<String, Faction> f) {
        MongoCollection<Document> speakerCollection = this.db.getCollection("Speaker");
        Map<String, Speaker> speakerMap = new HashMap<>();
        for (Document doc : speakerCollection.find()) {
            speakerMap.put(doc.getString("_id"), new Speaker_MongoDB_Impl(doc, p, f));
        }
        return speakerMap;
    }

    /**
     * Save the party List to the database
     * @param parties the party List to save
     * @author Maximilian Chen
     */
    public void saveParty(List<Party> parties) {
        MongoCollection<Document> partyCollection = this.db.getCollection("Party");
        List<Document> partyList = new ArrayList<>();
        for (Party party : parties) {
            partyList.add(party.toDocument());
        }
        try {
            partyCollection.insertMany(partyList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Parties already exist in database. Please use updateParty() instead.");
            e.printStackTrace();
        }
    }

    /**
     * Update the party Map in the database
     * @param a the party Map to update
     * @author Maximilian Chen
     */
    public void updateParty(Map<String, Party> a) {
        MongoCollection<Document> partyCollection = this.db.getCollection("Party");
        List<Document> partyList = new ArrayList<>();
        for (String id : a.keySet()) {
            Party s = a.get(id);
            partyList.add(s.toDocument());
        }
        try {
            partyCollection.insertMany(partyList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Parties already exist in database. Updating instead.");
            e.printStackTrace();
            for (Document d : partyList) {
                partyCollection.replaceOne(new Document("_id", d.get("_id")), d);
            }
        }
    }

    /**
     * Get the parties from the database
     * @return the list of parties
     * @author Maximilian Chen
     */
    public List<Party> getParty(Map<String, Speaker> speakerMap) {
        MongoCollection<Document> partyCollection = this.db.getCollection("Party");
        List<Party> partyList = new ArrayList<>();
        for (Document doc : partyCollection.find()) {
            partyList.add(new Party_MongoDB_Impl(doc, speakerMap));
        }
        return partyList;
    }

    /**
     * Get the parties from the database
     * @return the list of parties
     * @author Maximilian Chen
     */
    public Map<String, Party> getParty() {
        MongoCollection<Document> partyCollection = this.db.getCollection("Party");
        Map<String, Party> partyList = new HashMap<>();
        for (Document doc : partyCollection.find()) {
            partyList.put(doc.getString("name"), new Party_MongoDB_Impl(doc));
        }
        return partyList;
    }

    /**
     * Save the factions List to the database
     * @param FactionList the faction List to save
     * @author Maximilian Chen
     */
    public void saveFaction(List<Faction> FactionList) {
        MongoCollection<Document> factionCollection = this.db.getCollection("Faction");
        List<Document> factionList = new ArrayList<>();
        for (Faction faction : FactionList) {
            factionList.add(faction.toDocument());
        }
        try {
            factionCollection.insertMany(factionList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Factions already exist in database. Please use updateFaction() instead.");
            e.printStackTrace();
        }
    }

    /**
     * Update the factions Map in the database
     * @param f the party Map to update
     * @author Maximilian Chen
     */
    public void updateFaction(Map<String, Faction> f) {
        MongoCollection<Document> factionCollection = this.db.getCollection("Faction");
        List<Document> factionList = new ArrayList<>();
        for (String id : f.keySet()) {
            Faction s = f.get(id);
            factionList.add(s.toDocument());
        }
        try {
            factionCollection.insertMany(factionList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Factions already exist in database. Updating instead.");
            e.printStackTrace();
            for (Document d : factionList) {
                factionCollection.replaceOne(new Document("_id", d.get("_id")), d);
            }
        }
    }

    /**
     * Get the factions from the database
     * @return the list of factions
     * @author Maximilian Chen
     */
    public List<Faction> getFaction(Map<String, Speaker> s) {
        MongoCollection<Document> factionCollection = this.db.getCollection("Faction");
        List<Faction> factionList = new ArrayList<>();
        for (Document doc : factionCollection.find()) {
            factionList.add(new Faction_MongoDB_Impl(doc, s));
        }
        return factionList;
    }

    /**
     * Get the factions from the database
     * @return the list of factions
     * @author Maximilian Chen
     */
    public Map<String, Faction> getFaction() {
        MongoCollection<Document> factionCollection = this.db.getCollection("Faction");
        Map<String, Faction> factionList = new HashMap<>();
        for (Document doc : factionCollection.find()) {
            factionList.put(doc.getString("Name"), new Faction_MongoDB_Impl(doc));
        }
        return factionList;
    }

    /**
     * Save the speech Map to the database
     * @param a the speech Map to save
     * @throws ParseException If the date could not be parsed
     * @author Maximilian Chen
     */
    public void saveSpeech(Map<String, Speech> a) throws ParseException {
        MongoCollection<Document> speechCollection = this.db.getCollection("Speech");
        List<Document> speechList = new ArrayList<>();
        for (String id : a.keySet()) {
            Speech s = a.get(id);
            Document doc = new Document();
            doc.append("_id", id);
            doc.append("text", s.getText());
            doc.append("speaker", s.getSpeaker().getID());
            doc.append("date", s.getProtocol().getDate());
            doc.append("protocol", s.getProtocol().getID());
            doc.append("dayTopic", s.getDayTopic().getTitle());
            List<Comment> comments = s.getComments();
            List<String> commentIDs = new ArrayList<>();
            for (Comment comment : comments) {
                commentIDs.add(comment.getID());
            }
            doc.append("comments", commentIDs);
        }
        try {
            speechCollection.insertMany(speechList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Speeches already exist in database. Please use updateSpeech() instead.");
            e.printStackTrace();
        }
    }

    /**
     * Update the speech Map in the database
     * @param a the speech Map to update
     * @author Maximilian Chen
     */
    public void updateSpeech(Map<Long, Speech> a) {
        assert true;
    }

    /**
     * Save the protocol List to the database
     * @param a the protocol List to save
     * @author Maximilian Chen
     */
    public void saveProtocol(List<Protocol> a) throws InputException {
        MongoCollection<Document> protocolCollection = this.db.getCollection("Protocol");
        List<Document> protocolList = new ArrayList<>();
        for (Protocol p : a) {
            protocolList.add(p.toDocument());
        }
        try {
            protocolCollection.insertMany(protocolList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Protocols already exist in database. Please use updateProtocol() instead.");
            e.printStackTrace();
        }
    }

    /**
     * Update the protocol List in the database
     * @param a the protocol List to update
     * @author Maximilian Chen
     */
    public void updateProtocol(List<Protocol> a) throws InputException {
        MongoCollection<Document> protocolCollection = this.db.getCollection("Protocol");
        List<Document> protocolList = new ArrayList<>();
        for (Protocol p : a) {
            protocolList.add(p.toDocument());
        }
        try {
            protocolCollection.insertMany(protocolList);
        } catch (MongoBulkWriteException e) {
            System.out.println("Protocols already exist in database. Updating instead.");
            e.printStackTrace();
            for (Document d : protocolList) {
                protocolCollection.replaceOne(new Document("_id", d.get("_id")), d);
            }
        }
    }

    /**
     * Method to get all Protocols in the DB in a List
     * @param speakerMap a Map of all Speakers in parliament
     * @return A List of all Protocols
     * @author Maximilian Chen
     */
    public List<Protocol> getProtocol(Map<String, Speaker> speakerMap) {
        MongoCollection<Document> protocolCollection = this.db.getCollection("Protocol");
        List<Protocol> protocolList = new ArrayList<>();
        int i = 0;
        for (Document doc : protocolCollection.find()) {
            protocolList.add(new Protocol_MongoDB_Impl(doc, speakerMap));
            i++;
            if (i % 100 == 0) {
                System.out.println("Loaded " + i + " protocols");
            }
        }
        return protocolList;
    }

    /**
     * Method to get all Protocols as Documents in the DB in a List
     * @return A list od all protocol Documents
     * @author Maximilian Chen
     */
    public List<Document> getProtocolDocuments() {
        MongoCollection<Document> protocolCollection = this.db.getCollection("Protocol");
        List<Document> protocolList = new ArrayList<>();
        for (Document doc : protocolCollection.find()) {
            protocolList.add(doc);
        }
        return protocolList;
    }

    /**
     * Returns the speaker with the specified ID
     * @param id the ID of the speaker
     * @return the speaker with the specified ID
     * @author Maximilian Chen
     */
    public Speaker getSpeakerByID(String id, Map<String, Party> partyMap) {
        MongoCollection<Document> speakerCollection = this.db.getCollection("Speaker");
        Document doc = speakerCollection.find(Filters.eq("_id", id)).first();
        if (doc == null) {
            return null;
        }
        return new Member_MongoDB_Impl(doc, partyMap);
    }

    /**
     * Returns the speech with the specified ID
     * @param id         the ID of the speech
     * @param speakerMap the Map of speakers
     * @return the speech with the specified ID
     * @author Maximilian Chen
     */
    public Speech getSpeechByID(String id, Map<String, Speaker> speakerMap) {
        MongoCollection<Document> protocolCollection = this.db.getCollection("Protocol");
        BasicDBObject query = new BasicDBObject("dayTopic.speech._id", id);
        Document doc = protocolCollection.find(query).first();
        if (doc == null) {
            return null;
        }
        Protocol protocol = new Protocol_MongoDB_Impl(doc, speakerMap);
        for (DayTopic dayTopic : protocol.getDayTopicList()) {
            for (Speech speech : dayTopic.getSpeechList()) {
                if (speech.getID().equals(id)) {
                    speech.setDayTopic(dayTopic);
                    return speech;
                }
            }
        }
        return null;
    }

    /**
     * Analyses all speeches in all protocols and writes them into their own collection
     * @throws UIMAException the exception thrown, when creating a new JCas
     * @author Maximilian Chen
     */
    public void speechAnalysis() throws UIMAException {
        MongoCollection<Document> protocolCollection = this.db.getCollection("Protocol");
        MongoCollection<Document> speechAnalyzedCollection = this.db.getCollection("nlpSpeech");
        MongoCollection<Document> commentAnalyzedCollection = this.db.getCollection("nlpComment");
        List<Document> protocols = protocolCollection.find().into(new ArrayList<>());
        for (Document protocol : protocols) {
            System.out.println("Getting Protocols");
            List<Document> dayTopics = protocol.getList("dayTopic", Document.class);
            for (Document dayTopic : dayTopics) {
                List<Document> speeches = dayTopic.getList("speech", Document.class);
                List<String> speechList = new ArrayList<>();
                for (Document speech : speeches) {
                    if (Objects.equals(speech.getString("Analyzed"), "true")) {
                        continue;
                    }
                    if (speechAnalyzedCollection.find(new Document("_id", speech.getString("_id"))).first() != null) {
                        continue;
                    }

                    Speech speechObj = new Speech_MongoDB_Impl(speech);

                    JCas jCas;
                    jCas = speechObj.toJCas();
                    jCas = nlpTool.analyze(jCas);
                    speechObj.setJCas(jCas);

                    Document analysisResult = speechObj.toDocumentNLP();
                    speech.append("Analyzed", "true");
                    speech.append("NamedEntities", analysisResult.get("NamedEntities"));
                    speech.append("POS", analysisResult.get("POS"));
                    speech.append("Lemmas", analysisResult.get("Lemmas"));
                    speech.append("Tokens", analysisResult.get("Tokens"));
                    speech.append("Sentiment", analysisResult.get("Sentiment"));
                    speech.append("SentimentWhole", analysisResult.get("SentimentWhole"));
                    speech.append("Sentences", analysisResult.get("Sentences"));
                    speech.append("CoarseValue", analysisResult.get("CoarseValue"));
                    speech.append("ddcList", analysisResult.get("ddcList"));
                    speech.append("scoreList", analysisResult.get("scoreList"));
                    speech.append("Persons", analysisResult.get("Persons"));
                    speech.append("Locations", analysisResult.get("Locations"));
                    speech.append("Organisations", analysisResult.get("Organisations"));
                    speech.append("JCas", analysisResult.get("JCas"));

                    for (Document comment : speech.getList("Comment", Document.class)) {
                        if (commentAnalyzedCollection.find(new Document("_id", comment.getString("_id"))).first() != null) {
                            continue;
                        }

                        Comment commentObj = new Comment_MongoDB_Impl(comment);
                        JCas jCasComment;
                        jCasComment = commentObj.toJCas();
                        jCasComment = nlpTool.analyze(jCasComment);
                        commentObj.setJCas(jCasComment);

                        Document analysisResultComment = commentObj.toDocumentNLP();
                        comment.append("Analyzed", "true");
                        comment.append("NamedEntities", analysisResultComment.get("NamedEntities"));
                        comment.append("POS", analysisResultComment.get("POS"));
                        comment.append("Lemmas", analysisResultComment.get("Lemmas"));
                        comment.append("Tokens", analysisResultComment.get("Tokens"));
                        comment.append("Sentiment", analysisResultComment.get("Sentiment"));
                        comment.append("SentimentWhole", analysisResult.get("SentimentWhole"));
                        comment.append("Sentences", analysisResultComment.get("Sentences"));
                        comment.append("CoarseValue", analysisResultComment.get("CoarseValue"));
                        comment.append("ddcList", analysisResultComment.get("ddcList"));
                        comment.append("scoreList", analysisResultComment.get("scoreList"));
                        comment.append("Persons", analysisResultComment.get("Persons"));
                        comment.append("Locations", analysisResultComment.get("Locations"));
                        comment.append("Organisations", analysisResultComment.get("Organisations"));
                        comment.append("JCas", analysisResultComment.get("JCas"));

                        commentAnalyzedCollection.insertOne(comment);
                    }
                    if (!speechList.contains(speech.getString("_id"))) {
                        speech.remove("Comment");
                        speechAnalyzedCollection.insertOne(speech);
                    }

                    speechList.add(speechObj.getID());
                }
            }
        }
    }

    /**
     * Returns the DataBase
     * @return the DataBase
     * @author Maximilian Chen
     */
    public MongoDatabase getDatabase() {
        return this.db;
    }

    /**
     * Method to insert a document into a collection
     * @param pCollection : String
     * @param pDocument   : Document
     * @author Kenan Khauto
     */
    public void insert(String pCollection, Document pDocument) {

        MongoCollection<Document> collection = db.getCollection(pCollection);

        collection.insertOne(pDocument);
    }

    /**
     * Methode to get a document by id from a collection
     * @param id          : String
     * @param sCollection : MongoCollection
     * @return rDoc
     * @author Kenan Khauto
     */
    public Document getDocumentById(String id, String sCollection) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", id);

        FindIterable<Document> result = this.db.getCollection(sCollection).find(query);

        Document rDoc = null;

        for (Document document : result) {
            rDoc = document;
        }

        return rDoc;
    }


    /**
     * get all users from database
     * @return list of users as a list of bson documents
     * @author Kenan Khauto
     */
    public List<Document> getUsers() {
        List<Document> users = new ArrayList<>();
        MongoCollection<Document> collection = this.db.getCollection("user");
        BasicDBObject query = new BasicDBObject();
        collection.find(query).forEach((Block<? super Document>) users::add);

        return users;
    }

    /**
     * delete user from database
     * @param username as a string
     * @author Kenan Khauto
     */
    public void deleteUser(String username) {
        MongoCollection<Document> userCollection = db.getCollection("user");
        userCollection.deleteOne(new Document("username", username));
    }


    /**
     * update a user
     * @param username what user to update
     * @param field    which field to update
     * @param update   what is the update
     * @author Kenan Khauto
     */
    public void updateUser(String username, String field, String update) {
        MongoCollection<Document> userCollection = db.getCollection("user");

        Bson updates = Updates.combine(
                Updates.set("role", update)
        );

        Document query = new Document().append("_id", username);

        userCollection.updateOne(query, updates);

    }

    /**
     * get a user by id (username)
     * @param id username
     * @author Kenan Khauto
     */
    public Document getUserById(String id) {
        return this.getDocumentById(id, "user");
    }

    /**
     * update a field in a document in a collection
     * @param id         string
     * @param field      string
     * @param update     string
     * @param collection string
     * @author Kenan Khauto
     */
    public void updateDocument(String id, String field, String update, String collection) {
        MongoCollection<Document> pCollection = db.getCollection(collection);

        Bson updates = Updates.combine(
                Updates.set(field, update)
        );

        Document query = new Document().append("_id", id);

        pCollection.updateOne(query, updates);
    }

    /**
     * update a field in a document in a collection
     * for using long as a parameter
     * @param id         string
     * @param field      string
     * @param update     long
     * @param collection string
     * @author Kenan Khauto
     */
    public void updateDocument(String id, String field, long update, String collection) {
        MongoCollection<Document> pCollection = db.getCollection(collection);

        Bson updates = Updates.combine(
                Updates.set(field, update)
        );

        Document query = new Document().append("_id", id);

        pCollection.updateOne(query, updates);
    }

    /**
     * update a field in a document in a collection
     * used to use date as parameter
     * @param id         string
     * @param field      string
     * @param update     Date
     * @param collection string
     * @author Kenan Khauto
     */
    public void updateDocument(String id, String field, Date update, String collection) {
        MongoCollection<Document> pCollection = db.getCollection(collection);

        Bson updates = Updates.combine(
                Updates.set(field, update)
        );

        Document query = new Document().append("_id", id);

        pCollection.updateOne(query, updates);
    }

    /**
     * Counts documents in given collection selected by query
     * @param query          Document selective query
     * @param collectionName Target collection name
     * @return Number of documents in collection selected by query
     * @author Simon Schuett
     */
    public long countDocuments(Document query, String collectionName) {
        MongoCollection<Document> collection = db.getCollection(collectionName);

        return collection.countDocuments(query);
    }

    /**
     * Retrieves objects from connected db by any given query.
     * Based on method by Giuseppe Abrami
     * @param query
     * @param collectionName
     * @return MongoCursor Result
     * @author Simon Schuett
     */
    public MongoCursor<Document> getObjects(Document query, String collectionName) {
        return this.db.getCollection(collectionName).find(query).cursor();
    }

    /**
     * Retrieves objects from connected db by any given query.
     * Based on method by Giuseppe Abrami
     * @param query
     * @param collectionName
     * @param limit
     * @return MongoCursor Result
     * @author Simon Schuett
     */
    public MongoCursor<Document> getObjects(Document query, String collectionName, int limit) {
        if (limit <= 0) return this.db.getCollection(collectionName).find(query).cursor();

        return this.db.getCollection(collectionName).find(query).limit(limit).cursor();
    }

}
