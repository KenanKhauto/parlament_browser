package org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.*;
import org.texttechnologylab.parliament_browser_3_4.database.MongoDBHandler;
import org.texttechnologylab.parliament_browser_3_4.exceptions.PropertiesException;

import java.util.*;

public class Factory_MongoDB_Impl {
    private final MongoDatabase db;
    private final Map<String, Protocol> protocolAggregates = new HashMap<>();
    private List<Protocol> protocols = null;
    private List<Document> protocolDocuments = null;
    private Map<String, Speaker> speakersByID = null;
    private Map<String, Speaker> speakersByLastName = null;
    private Map<String, Speaker> speakersByFirstName = null;
    private Map<String, Party> parties = null;
    private Map<String, Speech> speeches = null;
    private Map<String, Faction> factions = null;

    /**
     * Constructor for the factory
     * @author Maximilian Chen
     */
    public Factory_MongoDB_Impl() {
        MongoDBHandler client = null;
        try {
            client = new MongoDBHandler("src/main/resources/PRG_WiSe22_Group_3_4.txt");
        } catch (PropertiesException e) {
            System.out.println("Properties file not found.");
        }
        assert client != null;
        this.db = client.getDatabase();
    }

    /**
     * Returns and caches the protocols as documents
     * @return a list of all protocol documents in the DB
     * @author Maximilian Chen
     */
    public List<Document> getProtocolDocuments() {
        if (this.protocolDocuments == null) {
            MongoCollection<Document> protocolCollection = db.getCollection("Protocol");
            List<Document> protocols = new ArrayList<>();
            for (Document doc : protocolCollection.find()) {
                protocols.add(doc);
            }
            this.protocolDocuments = protocols;
            return protocols;
        } else {
            return this.protocolDocuments;
        }
    }

    /**
     * Returns and caches all protocols
     * @return a list of all protocols in the DB
     * @author Maximilian Chen
     */
    public List<Protocol> getProtocols() {
        if (this.protocols == null && this.protocolDocuments == null) {
            MongoCollection<Document> protocolCollection = db.getCollection("Protocol");
            List<Protocol> protocols = new ArrayList<>();
            for (Document doc : protocolCollection.find()) {
                protocols.add(new Protocol_MongoDB_Impl(doc, getSpeakers()));
            }
            this.protocols = protocols;
            return protocols;
        } else if (this.protocols == null) {
            List<Protocol> protocols = new ArrayList<>();
            for (Document doc : this.protocolDocuments) {
                protocols.add(new Protocol_MongoDB_Impl(doc, getSpeakers()));
            }
            this.protocols = protocols;
            return protocols;
        } else {
            return this.protocols;
        }
    }

    /**
     * Returns and caches all speakers
     * @return a list of all speakers in the DB
     * @author Maximilian Chen
     */
    public Map<String, Speaker> getSpeakers() {
        if (this.speakersByID == null) {
            MongoCollection<Document> speakerCollection = db.getCollection("Speaker");
            Map<String, Speaker> speakers = new HashMap<>();
            for (Document doc : speakerCollection.find()) {
                Speaker sp = new Speaker_MongoDB_Impl(doc, getParty(), getFaction());
                speakers.put(sp.getID(), sp);
            }
            this.speakersByID = speakers;
            return speakers;
        } else {
            return this.speakersByID;
        }
    }


    /**
     * get speakers map with name as Key
     * @return
     */
    public Map<String, Speaker> getSpeakersV2() {
        if (this.speakersByID == null) {
            MongoCollection<Document> speakerCollection = db.getCollection("Speaker");
            Map<String, Speaker> speakers = new HashMap<>();
            for (Document doc : speakerCollection.find()) {
                Speaker sp = new Speaker_MongoDB_Impl(doc, getParty());
                speakers.put(sp.getName(), sp);
            }
            this.speakersByID = speakers;
            return speakers;
        } else {
            return this.speakersByID;
        }
    }

    /**
     * Returns and caches all parties
     * @return a list of all parties in the DB
     * @author Maximilian Chen
     */
    private Map<String, Party> getParty() {
        if (this.parties == null) {
            MongoCollection<Document> partyCollection = db.getCollection("Party");
            Map<String, Party> parties = new HashMap<>();
            for (Document doc : partyCollection.find()) {
                Party party = new Party_MongoDB_Impl(doc);
                parties.put(party.getName(), party);
            }
            this.parties = parties;
            return parties;
        } else {
            return this.parties;
        }
    }

    /**
     * Returns and caches all factions
     * @return a list of all factions in the DB
     * @author Maximilian Chen
     */
    private Map<String, Faction> getFaction() {
        if (this.factions == null) {
            MongoCollection<Document> factionCollection = db.getCollection("Faction");
            Map<String, Faction> factions = new HashMap<>();
            for (Document doc : factionCollection.find()) {
                Faction faction = new Faction_MongoDB_Impl(doc);
                factions.put(faction.getName(), faction);
            }
            this.factions = factions;
            return factions;
        } else {
            return this.factions;
        }
    }

    /**
     * returns a map of all parties with the name as key
     * @return map of parties
     * @author Maximilian Chen
     */
    public Map<String, Party> getParties() {
        getSpeakers();
        linkParty();
        return this.parties;
    }

    /**
     * returns a map of all factions with the name as key
     * @return map of factions
     */
    public Map<String, Faction> getFactions() {
        getSpeakers();
        linkFaction();
        return this.factions;
    }

    /**
     * links all speakers to their parties
     * @author Maximilian Chen
     */
    public void linkParty() {
        for (String ID : this.speakersByID.keySet()) {
            if (this.speakersByID.get(ID).getParty() != null) {
                this.speakersByID.get(ID).getParty().addMember(this.speakersByID.get(ID));
            }
        }
    }

    /**
     * links all speakers to their factions
     * @author Maximilian Chen
     */
    public void linkFaction() {
        for (String ID : this.speakersByID.keySet()) {
            if (this.speakersByID.get(ID).getFaction() != null) {
                this.speakersByID.get(ID).getFaction().addMember(this.speakersByID.get(ID));
            }
        }
    }

    /**
     * Returns and caches all speeches
     * @return a Map of all speeches in the DB with their ID as key
     * @author Maximilian Chen
     */
    public Map<String, Speech> getSpeeches() {
        if (this.speeches == null) {
            List<Document> protocols = getProtocolDocuments();
            Map<String, Speech> speeches = new HashMap<>();
            for (Document protocol : protocols) {
                for (Document dayTopic : protocol.getList("dayTopic", Document.class)) {
                    for (Document speech : dayTopic.getList("speech", Document.class)) {
                        speeches.put(speech.getString("_id"), new Speech_MongoDB_Impl(speech, getSpeakers()));
                    }
                }
            }
            this.speeches = speeches;
            return speeches;
        } else {
            return this.speeches;
        }
    }

    /**
     * Gets any document from the DB
     * @param Collection the collection to search in
     * @param Attribute  the attribute to search in
     * @param Name       the name to search for
     * @return the document if found, null if not found
     * @author Maximilian Chen
     */
    public Document getDocument(String Collection, String Attribute, String Name) {
        MongoCollection<Document> docCollection = db.getCollection(Collection);
        AggregateIterable<Document> docList = docCollection.aggregate(Collections.singletonList(new Document().append("$match", new Document().append(Attribute, Name))));
        if (docList.first() == null) {
            return null;
        } else {
            return docList.first();
        }
    }

    /**
     * Gets a speaker by FirstName
     * @param Name the FirstName of the speaker
     * @return the speaker if found, null if not found
     * @author Maximilian Chen
     */
    public Speaker getSpeakerByFirstName(String Name) {
        if (this.speakersByFirstName == null) {
            this.speakersByFirstName = new HashMap<>();
            Map<String, Speaker> speakers = getSpeakers();
            for (String ID : speakers.keySet()) {
                this.speakersByFirstName.put(speakers.get(ID).getFirstName(), speakers.get(ID));
            }
        }
        return this.speakersByFirstName.get(Name);
    }

    /**
     * Gets a speaker by LastName
     * @param Name the Lastname of the speaker
     * @return the speaker if found, null if not found
     * @author Maximilian Chen
     */
    public Speaker getSpeakerByLastName(String Name) {
        if (this.speakersByLastName == null) {
            this.speakersByLastName = new HashMap<>();
            Map<String, Speaker> speakers = getSpeakers();
            for (String ID : speakers.keySet()) {
                this.speakersByLastName.put(speakers.get(ID).getLastName(), speakers.get(ID));
            }
        }
        return this.speakersByLastName.get(Name);
    }

    /**
     * Gets a speaker by ID
     * @param ID the ID of the speaker
     * @return the speaker if found, null if not found
     * @author Maximilian Chen
     */
    public Speaker getSpeakerByID(String ID) {
        if (this.speakersByID == null) {
            getSpeakers();
        }
        return this.speakersByID.get(ID);
    }

    /**
     * Gets speaker from the DB by ID using aggregation
     * @param ID the ID of the speaker
     * @return the speaker if found, null if not found
     * @author Maximilian Chen
     */
    public Speaker getSpeakerByIDAg(String ID) {
        MongoCollection<Document> speakerCollection = db.getCollection("Speaker");
        AggregateIterable<Document> speakerList = speakerCollection.aggregate(Collections.singletonList(new Document().append("$match", new Document().append("_id", ID))));
        if (speakerList.first() == null) {
            return null;
        } else {
            return new Speaker_MongoDB_Impl(Objects.requireNonNull(speakerList.first()), getParty(), getFaction());
        }
    }

    /**
     * gets speaker from the DB by attribute using aggregation
     * @param Attribute the attribute to search in
     * @param Name      the name to search for
     * @return the speaker if found, null if not found
     * @author Maximilian Chen
     */
    public Speaker getSpeakerByAttributeAg(String Attribute, String Name) {
        MongoCollection<Document> speakerCollection = db.getCollection("Speaker");
        AggregateIterable<Document> speakerList = speakerCollection.aggregate(Collections.singletonList(new Document().append("$match", new Document().append(Attribute, Name))));
        if (speakerList.first() == null) {
            return null;
        } else {
            return new Speaker_MongoDB_Impl(Objects.requireNonNull(speakerList.first()), getParty(), getFaction());
        }
    }

    /**
     * Gets speaker from the DB by ID using aggregation
     * @param ID the ID of the protocol
     * @return the protocol if found, null if not found
     * @author Maximilian Chen
     */
    public Protocol getProtocolByIDAg(String ID) {
        if (this.protocolAggregates.containsKey(ID)) {
            return this.protocolAggregates.get(ID);
        } else {
            MongoCollection<Document> protocolCollection = db.getCollection("Protocol");
            AggregateIterable<Document> protocolList = protocolCollection.aggregate(Collections.singletonList(new Document().append("$match", new Document().append("_id", ID))));
            if (protocolList.first() == null) {
                return null;
            } else {
                Protocol protocol = new Protocol_MongoDB_Impl(Objects.requireNonNull(protocolList.first()), getSpeakers());
                this.protocolAggregates.put(ID, protocol);
                return protocol;
            }
        }
    }

    /**
     * gets a list of protocol IDs
     * @return the list of protocol IDs
     * @author Maximilian Chen
     */
    public List<String> getProtocolIDs() {
        MongoCollection<Document> protocolCollection = db.getCollection("Protocol");
        AggregateIterable<Document> protocolList = protocolCollection.aggregate(Collections.singletonList(Aggregates.group("$_id")));
        List<String> protocolIDs = new ArrayList<>();
        for (Document doc : protocolList) {
            protocolIDs.add(doc.getString("_id"));
        }
        return protocolIDs;
    }

    /**
     * gets a dayTopic by ID
     * @param ID the ID of the dayTopic
     * @return the dayTopic if found, null if not found
     * @author Maximilian Chen
     */
    public DayTopic getDayTopicByIDAg(String ID) {
        MongoCollection<Document> protocolCollection = db.getCollection("Protocol");
        BasicDBObject query = new BasicDBObject("dayTopic._id", ID);
        Document doc = protocolCollection.find(query).first();
        if (doc == null) {
            return null;
        } else {
            for (Document dayTopicDoc : doc.getList("dayTopic", Document.class)) {
                if (dayTopicDoc.getString("_id").equals(ID)) {
                    return new DayTopic_MongoDB_Impl(dayTopicDoc, getSpeakers());
                }
            }
            return null;
        }
    }

    /**
     * gets a speech by ID
     * @param ID the ID of the speech
     * @return the speech if found, null if not found
     */

    public Speech getSpeechByIDAg(String ID) {
        MongoCollection<Document> protocolCollection = db.getCollection("Protocol");
        BasicDBObject query = new BasicDBObject("dayTopic.speech._id", ID);
        Document doc = protocolCollection.find(query).first();
        if (doc != null) {
            for (Document dayTopicDoc : doc.getList("dayTopic", Document.class)) {
                for (Document speechDoc : dayTopicDoc.getList("speech", Document.class)) {
                    if (speechDoc.getString("_id").equals(ID)) {
                        return new Speech_MongoDB_Impl(speechDoc, getSpeakers());
                    }
                }
            }
        }
        return null;
    }
}
