package org.texttechnologylab.parliament_browser_3_4;

//import org.neo4j.kernel.impl.store.format.RecordFormats;

import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Factory_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.Parliament;
import org.texttechnologylab.parliament_browser_3_4.data.Protocol;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Parliament_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.database.MongoDBHandler;
import org.texttechnologylab.parliament_browser_3_4.database.MultiNLP;
import org.texttechnologylab.parliament_browser_3_4.exceptions.PropertiesException;
import org.texttechnologylab.parliament_browser_3_4.nlp.NLPAnalyzeToMongo;
import org.texttechnologylab.parliament_browser_3_4.webservice.Webserver;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.texttechnologylab.parliament_browser_3_4.nlp.NLPAnalyzeToMongo.nlpTool;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException, UIMAException, URISyntaxException, PropertiesException {

        webserverStartup();

        // simonTest();
        //kenanTest();
        // maxTest();
        //stanTest();
    }

    /**
     * Main Java Spark Startup Function.
     * @author Simon Schütt
     */
    public static void webserverStartup() {
        MongoDBHandler client = null;
        try {
            client = new MongoDBHandler("src/main/resources/PRG_WiSe22_Group_3_4.txt");
        } catch (PropertiesException e) {
            System.out.println("Properties file not found.");
        }
        assert client != null;

        Factory_MongoDB_Impl parlFac = new Factory_MongoDB_Impl();

        Webserver server = new Webserver(parlFac, client);
        server.init();
    }

    /**
     * Only for test purposes
     */
    public static void maxTest() {
        MongoDBHandler client = null;
        try {
            client = new MongoDBHandler("src/main/resources/PRG_WiSe22_Group_3_4.txt");
        } catch (PropertiesException e) {
            System.out.println("Properties file not found.");
        }
        assert client != null;

        Factory_MongoDB_Impl f = new Factory_MongoDB_Impl();
        //client.speakerToMongo("src/main/resources/MDB_STAMMDATEN.xml");

        List<Document> docs = f.getProtocolDocuments();

        int countS = 0;
        int countC = 0;
        for (Document protocol : docs) {
            for (Document dayTopic : protocol.getList("dayTopic", Document.class)) {
                for (Document speech : dayTopic.getList("speech", Document.class)) {
                    countS++;
                    for (Document comment : speech.getList("Comment", Document.class)) {
                        countC++;
                    }
                }
            }
        }

        System.out.println(docs.size());
        System.out.println(countS);
        System.out.println(countC);

        MongoCollection<Document> nlpSpeechCollection = client.getDatabase().getCollection("nlpSpeech");
        MongoCollection<Document> nlpCommentCollection = client.getDatabase().getCollection("nlpComment");

        AggregateIterable<Document> protocolExistingID = nlpSpeechCollection.aggregate(Arrays.asList(
                Aggregates.group("$_id")
        ));
        AggregateIterable<Document> commentExistingID = nlpCommentCollection.aggregate(Arrays.asList(
                Aggregates.group("$_id")
        ));

        List<String> protocolExistingIDs = new ArrayList<>();
        for (Document doc : protocolExistingID) {
            protocolExistingIDs.add(doc.getString("_id"));
        }

        List<String> commentExistingIDs = new ArrayList<>();
        for (Document doc : commentExistingID) {
            commentExistingIDs.add(doc.getString("_id"));
        }

        int threadCount = 8;

        List<List<Document>> a = Lists.partition(docs, docs.size() / threadCount);

        for (List<Document> b : a) {
            Runnable r = new MultiNLP(client, b, protocolExistingIDs, commentExistingIDs);
            new Thread(r).start();
        }

        //client.speakerToMongo("C:\\Users\\Maximilian\\Documents\\!Studium\\Informatik Bachelor\\MdB-Stammdaten-data");
        //client.factionToMongo("C:\\Users\\Maximilian\\Documents\\!Studium\\Informatik Bachelor\\Protokolle_Bundestag_19");*/
    }

    /**
     * Only for test purposes
     */
    public static void simonTest() {

    }

    /**
     * Only for test purposes
     */
    public static void kenanTest() throws ParserConfigurationException, IOException, SAXException {
        String pathToSta = "C:\\Users\\kenan\\Desktop\\WISe-23\\Programmierpraktikum\\MatrialenUEB1\\MdB-Stammdaten-data";

        System.out.println("Start loading ...");
        Parliament p = new Parliament_File_Impl(pathToSta);
        System.out.println("Finished loading!\n");

        MongoDBHandler client = null;
        try {
            client = new MongoDBHandler("src/main/resources/PRG_WiSe22_Group_3_4.txt");
        } catch (PropertiesException e) {
            System.out.println("Properties file not found.");
        }
        assert client != null;
        /*System.out.println("-----------------------------------");
        System.out.println("Factions:");
        p.getFactory().getFactions().forEach(System.out::println);
        System.out.println("-----------------------------------");
        System.out.println("Parties: ");
        p.getFactory().getParties().forEach(System.out::println);
        System.out.println("-----------------------------------");
        System.out.println("Speakers: ");
        p.getFactory().getSpeakers().forEach(speaker -> {
            System.out.println(speaker.getTitle() + speaker.getName() + ", party: " + speaker.getParty()
                    + ", faction: " + speaker.getFaction() + ", role: " + speaker.getRole());
        });
        System.out.println("-----------------------------------");
        System.out.println("Protocols: ");
        p.getFactory().getProtocols().forEach(pr -> {

            System.out.println("---------------------");
            System.out.println("protocol: " + pr.getID());

            pr.getDayTopicList().forEach(dayTopic -> {
                System.out.println("==================");
                System.out.println("\tname: " + dayTopic.getName());
                System.out.println("\ttitle: " + dayTopic.getTitle());
                System.out.println("\tspeech num: " + dayTopic.getSpeechList().size());

            });
        });
        Speech speech = p.getFactory().getSpeeches(p.getProtocols().get(0)).stream().findFirst().get();

        System.out.println(speech.getText());

        speech.getComments().forEach(comment -> {
            System.out.println("=====================");
            System.out.println(comment.getContent());
            System.out.println(comment.getIndex());
        });

        p.getFactory().getSpeakers().forEach(s -> {
            System.out.println(s.getName());
        });*/
        MongoDBHandler finalClient = client;
        /*p.getFactory().getSpeeches().forEach(speech -> {
            speech.getComments().forEach(comment -> {
                finalClient.updateDocument(
                        comment.getID(),
                        "SpeakerID",
                        comment.findCommentator(p.getFactory().getParties(), p.getFactory().getSpeakers()),
                        "nlpComment"
                );
                System.out.println(comment.getID() + ": updated!");
            });
        });*/

        p.getFactory().getSpeeches().forEach(speech -> {
            try {
                finalClient.updateDocument(speech.getID(), "dateLong", speech.getProtocol().getDate().getTime(), "nlpSpeech");
                finalClient.updateDocument(speech.getID(), "dateISO", speech.getProtocol().getDate(), "nlpSpeech");
                System.out.println("id: " + speech.getID() + " updated!");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Only for test purposes
     */
    public static void stanTest() throws RuntimeException, UIMAException, IOException {
        JCas jCas, emptyCas = null;
        jCas = JCasFactory.createText("Nee, was war das doch für ein schöner Tag. Aber hin und wieder geht " +
                "mir FreeMarker echt auf den Sack", "de");

        jCas = nlpTool.analyze(jCas);

        for (Token token : JCasUtil.select(jCas, Token.class)) {
            System.out.println(" Token Test  : "
                    + token.getPos().getCoveredText() + "\t"
                    + token.getPos().getPosValue() + "\t"
                    + token.getPos().getCoarseValue() + "\t");
        }

        String jCasAsString = NLPAnalyzeToMongo.serialize(jCas);

        System.out.println("\n\n das is er: \n" + jCasAsString.getBytes() + "  \n der serialString \n\n");
        System.out.println("\n\n jetzt kommt deserialisierung !! \n\n");

        JCas whatever = NLPAnalyzeToMongo.deserialize(jCasAsString);

        for (Token token : JCasUtil.select(whatever, Token.class)) {
            System.out.println(" Token Test  : "
                    + token.getPos().getCoveredText() + "\t"
                    + token.getPos().getPosValue() + "\t"
                    + token.getPos().getCoarseValue() + "\t");
        }
    }

    public static List<Protocol> getDataFromMongo() {
        MongoDBHandler client = null;
        try {
            client = new MongoDBHandler(Objects.requireNonNull(Main.class.getClassLoader().getResource("PRG_WiSe22_Group_3_4.txt")).getPath());
        } catch (PropertiesException e) {
            System.out.println("Properties file not found.");
        }
        assert client != null;
        return client.getProtocol(null);
    }

}
