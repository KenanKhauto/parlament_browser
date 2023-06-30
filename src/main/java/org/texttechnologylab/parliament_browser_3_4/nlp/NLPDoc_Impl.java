package org.texttechnologylab.parliament_browser_3_4.nlp;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.hucompute.textimager.uima.type.Sentiment;
import org.texttechnologylab.annotation.AnnotationComment;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Class with method implementations for dealing with NLP related issues
 * @author Stanley Mathew
 */
public class NLPDoc_Impl implements NLPDoc {

    private final Document document = null;
    private JCas jcas;


    /**
     * Method that sets JCas
     * @param pCas
     */
    public void setCas(JCas pCas) {
        this.jcas = pCas;
    }


    /**
     * Method that returns JCas from document
     * @return JCas
     */
    public JCas toJCas() {

        try {
            jcas = JCasFactory.createText(document.getString("text"), "de");
        } catch (UIMAException e) {
            e.printStackTrace();
        }
        return jcas;
    }


    @Override
    public List<NamedEntity> getNamedEntities() {
        return JCasUtil.select(this.toJCas(), NamedEntity.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<Token> getTokens() {
        return JCasUtil.select(this.toJCas(), Token.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<POS> getPOS() {
        return JCasUtil.select(this.toJCas(), POS.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<Sentiment> getSentiments() {
        return JCasUtil.select(this.toJCas(), Sentiment.class).stream().collect(Collectors.toList());
    }


    @Override
    public Sentiment getDocumentSentiment() {
        List<AnnotationComment> annoComments = JCasUtil.select(this.toJCas(), AnnotationComment.class).stream().filter(d -> {

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

    @Override
    public List<Lemma> getLemmas() {
        return JCasUtil.select(this.toJCas(), Lemma.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<Sentence> getSentences() {
        return JCasUtil.select(this.toJCas(), Sentence.class).stream().collect(Collectors.toList());
    }

    @Override
    public boolean isProcessed() {
        return JCasUtil.selectAll(toJCas()).size() > 1;
    }


}
