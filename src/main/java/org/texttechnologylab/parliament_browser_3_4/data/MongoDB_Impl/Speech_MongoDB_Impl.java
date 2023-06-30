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
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;
import org.texttechnologylab.parliament_browser_3_4.data.Speech;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Faction_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Party_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Speech_File_Impl;
import org.texttechnologylab.uimadb.wrapper.mongo.MongoSerialization;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.CasSerializationException;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.SerializerInitializationException;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.UnknownFactoryException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Speech_MongoDB_Impl extends Speech_File_Impl implements Speech {
    private Document doc;
    private JCas jCas;
    private List<NamedEntity> namedEntities;
    private List<POS> posTags;
    private List<Lemma> lemmas;
    private List<Token> tokens;
    private List<Sentiment> sentiments;
    private List<Sentence> sentences;

    public Speech_MongoDB_Impl(Document doc, Map<String, Speaker> speakerMap) {
        setID(doc.getString("_id"));
        if (speakerMap.containsKey(doc.getString("SpeakerID"))) {
            setSpeaker(speakerMap.get(doc.getString("SpeakerID")));
        }
        // if SpeakerID in Speaker-Collection doesn't match ID in Protocol-Collection fetch Speaker object by
        // iterating through values of speakerMap and returning match to setSpeaker()-function
        else {
            AtomicReference<Boolean> fetched = new AtomicReference<>(false);
            speakerMap.values().forEach(elem -> {
                if (Objects.equals(elem.getName(), doc.getString("SpeakerName")) && !fetched.get()){
                    setSpeaker(elem);
                    fetched.set(true);
                }
            });
        }
        setText(doc.getString("Text"));
        List<Comment> comments = new ArrayList<>();
        if (doc.getList("Comment", Document.class) != null) {
            doc.getList("Comment", Document.class).forEach(commentDoc -> comments.add(new Comment_MongoDB_Impl(commentDoc)));
        }
        addComments(comments);
    }

    public Speech_MongoDB_Impl(Document doc) {
        setID(doc.getString("_id"));
        setText(doc.getString("Text"));
        List<Comment> comments = new ArrayList<>();
        if (doc.getList("Comment", Document.class) != null) {
            doc.getList("Comment", Document.class).forEach(commentDoc -> comments.add(new Comment_MongoDB_Impl(commentDoc)));
        }
        addComments(comments);
    }

    /**
     * Helper Method transforms List of annotations to List of Strings
     * @param annoList List of Annotations
     * @return List of Strings
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
    public Document toDocumentNoNLP() {
        Document doc = new Document();
        doc.append("_id", getID());
        doc.append("SpeakerName", getSpeaker().getName());
        doc.append("SpeakerID", getSpeaker().getID());
        doc.append("Text", getText());
        doc.append("Party", getSpeaker().getParty() != null ? this.getSpeaker().getParty().getName() : "parteilos");
        doc.append("Faction", getSpeaker().getFaction() != null ? this.getSpeaker().getFaction().getName() : "fraktionslos");
        List<Document> commentDocuments = new ArrayList<>();
        getComments().forEach(comment -> commentDocuments.add(comment.toDocumentNoNLP()));
        doc.append("Comment", commentDocuments);
        return doc;
    }

    public Document getDoc() {
        return this.doc;
    }

    public JCas toJCas() throws UIMAException {
        return JCasFactory.createText(getText(), "de");
    }

    public void setJCas(JCas jCas) {
        this.jCas = jCas;
    }

    public JCas getJCas() {
        return this.jCas;
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


    public List<Lemma> getLemmas() {
        return new ArrayList<>(JCasUtil.select(this.jCas, Lemma.class));
    }


    public List<Sentence> getSentences() {
        return new ArrayList<>(JCasUtil.select(this.jCas, Sentence.class));
    }


    public boolean isProcessed() {
        return JCasUtil.selectAll(this.jCas).size() > 1;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public JCas getjCas() {
        return jCas;
    }

    public void setjCas(JCas jCas) {
        this.jCas = jCas;
    }

    public void setNamedEntities(List<NamedEntity> namedEntities) {
        this.namedEntities = namedEntities;
    }

    public List<POS> getPosTags() {
        return posTags;
    }

    public void setPosTags(List<POS> posTags) {
        this.posTags = posTags;
    }

    public void setLemmas(List<Lemma> lemmas) {
        this.lemmas = lemmas;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void setSentiments(List<Sentiment> sentiments) {
        this.sentiments = sentiments;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }
}
