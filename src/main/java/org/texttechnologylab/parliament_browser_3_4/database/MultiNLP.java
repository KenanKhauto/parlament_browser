package org.texttechnologylab.parliament_browser_3_4.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.Comment;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Comment_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Speech_MongoDB_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.Speech;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.texttechnologylab.parliament_browser_3_4.nlp.NLPAnalyzeToMongo.nlpTool;

public class MultiNLP implements Runnable {
    private final MongoDatabase db;
    private final List<Document> protocols;
    private final List<String> processedProtocols;
    private final List<String> processedComments;

    /**
     * The constructor for a multithreaded implementation of the NLP analysis pipeline
     * @param client             MongoDBConnectionHandler to use to write the analysis results to
     * @param protocols          List of protocols to analyze
     * @param processedProtocols List of speech IDs of all previously analyzed speeches
     * @param processedComments  List of comment IDs of all previously analyzed comments
     * @author Maximilian Chen
     */
    public MultiNLP(MongoDBHandler client, List<Document> protocols, List<String> processedProtocols, List<String> processedComments) {
        this.db = client.getDatabase();
        this.protocols = protocols;
        this.processedProtocols = processedProtocols;
        this.processedComments = processedComments;
    }

    /**
     * The codeblock that is run, when thread starts
     * @author Maximilian Chen
     */
    public void run() {
        System.out.println("Thread " + Thread.currentThread().getId() + " is running");
        MongoCollection<Document> speechAnalyzedCollection = db.getCollection("nlpSpeech");
        MongoCollection<Document> commentAnalyzedCollection = db.getCollection("nlpComment");
        for (Document protocol : protocols) {
            System.out.println("Getting Protocols");
            List<Document> dayTopics = protocol.getList("dayTopic", Document.class);
            for (Document dayTopic : dayTopics) {
                List<Document> speeches = dayTopic.getList("speech", Document.class);
                List<String> speechList = new ArrayList<>();
                for (Document speech1 : speeches) {
                    Document speech = new Document(speech1);
                    if (Objects.equals(speech.getString("Analyzed"), "true")) {
                        continue;
                    }
                    if (processedProtocols.contains(speech.getString("_id"))) {
                        continue;
                    }
                    if (speechList.contains(speech.getString("_id"))) {
                        continue;
                    }
                    Speech speechObj = new Speech_MongoDB_Impl(speech);

                    JCas jCas;
                    try {
                        jCas = speechObj.toJCas();
                        jCas = nlpTool.analyze(jCas);
                    } catch (UIMAException e) {
                        e.printStackTrace();
                        continue;
                    }
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

                    for (Document comment1 : speech.getList("Comment", Document.class)) {
                        Document comment = new Document(comment1);
                        if (processedComments.contains(comment.getString("_id"))) {
                            continue;
                        }
                        if (speechList.contains(comment.getString("_id"))) {
                            continue;
                        }

                        Comment commentObj = new Comment_MongoDB_Impl(comment);
                        JCas jCasComment;
                        try {
                            jCasComment = commentObj.toJCas();
                            jCasComment = nlpTool.analyze(jCasComment);
                        } catch (UIMAException e) {
                            e.printStackTrace();
                            continue;
                        }
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
                        speechList.add(comment.getString("_id"));
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
}
