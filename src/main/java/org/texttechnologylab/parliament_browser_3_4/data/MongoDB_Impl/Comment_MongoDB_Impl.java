package org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.bson.Document;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.texttechnologylab.annotation.AnnotationComment;
import org.texttechnologylab.parliament_browser_3_4.data.Comment;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Comment_File_Impl;
import org.texttechnologylab.uimadb.wrapper.mongo.MongoSerialization;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.CasSerializationException;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.SerializerInitializationException;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.UnknownFactoryException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Comment_MongoDB_Impl extends Comment_File_Impl implements Comment {

    private Document doc;
    private JCas jCas;

    /**
     * Constructor for a new Comment getting a document from the database
     * @param doc the document from which to generate the comment
     * @author Maximilian Chen
     */
    public Comment_MongoDB_Impl(Document doc) {
        setID(doc.getString("_id"));
        setContent(doc.getString("content"));
    }

    /**
     * Helper Method transforms List of annotations to List of Strings
     * @param annoList List of Annotations
     * @return List of Strings
     * @author Maximilian Chen, Stanley Mathew
     */
    public static List<String> toStringList(List<Annotation> annoList) {
        List<String> stringList = new ArrayList<>(0);
        annoList.forEach(anno -> {
            if (anno instanceof POS) {
                stringList.add(((POS) anno).getPosValue());
            } else {
                stringList.add(anno.getCoveredText());
            }
        });
        return stringList;
    }

    /**
     * Turns the comment into a document while ignoring NLP elements
     * @return The comment in document form without NLP elements
     * @author Maximilian Chen
     */
    @Override
    public Document toDocumentNoNLP() {
        Document doc = new Document();
        doc.append("_id", this.getID());
        doc.append("content", this.getContent());
        doc.append("speechID", this.getSpeech().getID());
        doc.append("SpeakerID", this.getSpeech().getSpeaker().getID());
        try {
            doc.append("protocolID", "" + this.getSpeech().getProtocol().getNum());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Turns the comment into a document with NLP elements
     * @return The comment in document form with NLP elements
     * @author Maximilian Chen
     */
    @Override
    public Document toDocumentNLP() {
        Document doc = new Document();
        doc.append("PosValue", (new ArrayList<>(JCasUtil.select(jCas, Token.class)).stream().map(tok -> tok.getPos().getPosValue()).collect(Collectors.toList())));
        doc.append("CoarseValue", (new ArrayList<>(JCasUtil.select(jCas, Token.class)).stream().map(tok -> tok.getPos().getCoarseValue()).collect(Collectors.toList())));
        doc.append("ddcList", (new ArrayList<>(JCasUtil.select(jCas, CategoryCoveredTagged.class)).stream().map(CategoryCoveredTagged::getValue).collect(Collectors.toList())));
        doc.append("scoreList", (new ArrayList<>(JCasUtil.select(jCas, CategoryCoveredTagged.class)).stream().map(CategoryCoveredTagged::getScore).collect(Collectors.toList())));
        doc.append("POS", toStringList(new ArrayList<>(getPOS())));
        doc.append("NamedEntities", toStringList(new ArrayList<>(getNamedEntities())));
        doc.append("Lemmas", toStringList(new ArrayList<>(getLemmas())));
        doc.append("Tokens", toStringList(new ArrayList<>(getTokens())));
        doc.append("Sentences", toStringList(new ArrayList<>(getSentences())));
        doc.append("Persons", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("PER")).collect(Collectors.toList())));
        doc.append("Locations", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("LOC")).collect(Collectors.toList())));
        doc.append("Organisations", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("ORG")).collect(Collectors.toList())));
        doc.append("Persons", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("PER")).collect(Collectors.toList())));
        doc.append("Locations", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("LOC")).collect(Collectors.toList())));
        doc.append("Organisations", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("ORG")).collect(Collectors.toList())));
        ArrayList<Document> sentence = new ArrayList<>();
        for (Sentence se : JCasUtil.select(jCas, Sentence.class)) {
            Document nlpSentence = new Document();
            nlpSentence.put("text", se.getCoveredText());
            nlpSentence.put("sentiment", JCasUtil.selectCovered(Sentiment.class, se).get(0).getSentiment());
            sentence.add(nlpSentence);
        }
        doc.append("Sentiment", sentence);
        if (getDocumentSentiment() != null) {
            doc.append("SentimentWhole", getDocumentSentiment().getSentiment());
        } else {
            doc.append("SentimentWhole", getSentiments().stream().mapToDouble(Sentiment::getSentiment).sum());
        }
        try {
            doc.append("JCas", MongoSerialization.serializeJCas(this.jCas));
        } catch (UnknownFactoryException | SerializerInitializationException | CasSerializationException e) {
            e.printStackTrace();
        }
        doc.append("Analyzed", "true");
        return doc;
    }

    @Override
    public JCas toJCas() throws UIMAException {
        JCas jCas = JCasFactory.createJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(getContent());
        return jCas;
    }

    public JCas toJCas(JCas jCas) {
        jCas.reset();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(getContent());
        return jCas;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public JCas getJCas() {
        return jCas;
    }

    public void setJCas(JCas jCas) {
        this.jCas = jCas;
    }

    public List<NamedEntity> getNamedEntities() {
        return new ArrayList<>(JCasUtil.select(this.jCas, NamedEntity.class));
    }

    public List<Token> getTokens() {
        return new ArrayList<>(JCasUtil.select(this.jCas, Token.class));
    }

    public List<POS> getPOS() {
        return new ArrayList<>(JCasUtil.select(this.jCas, POS.class));
    }

    public List<Sentiment> getSentiments() {
        return new ArrayList<>(JCasUtil.select(this.jCas, Sentiment.class));
    }

    public List<Lemma> getLemmas() {
        return new ArrayList<>(JCasUtil.select(this.jCas, Lemma.class));
    }


    public List<Sentence> getSentences() {
        return new ArrayList<>(JCasUtil.select(this.jCas, Sentence.class));
    }

    public Sentiment getDocumentSentiment() {
        List<AnnotationComment> annoComments = JCasUtil.select(this.jCas, AnnotationComment.class).stream().filter(d -> {

            if (d.getReference() instanceof Sentiment) {
                return d.getValue().equalsIgnoreCase("text");
            }
            return false;

        }).collect(Collectors.toList());

        if (annoComments.size() >= 1) {
            return (Sentiment) annoComments.get(0).getReference();
        }

        return null;
    }


}

