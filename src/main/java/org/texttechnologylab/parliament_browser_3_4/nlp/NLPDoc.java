package org.texttechnologylab.parliament_browser_3_4.nlp;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.Sentiment;

import java.util.List;


/**
 * Interface for interacting with NLP classes
 * @author Stanley Mathew
 */

public interface NLPDoc {


    /**
     * Transform to JCas
     * @return JCas
     * @throws UIMAException
     */
    JCas toJCas() throws UIMAException;


    /**
     * Get Named Entities
     * @return List<NamedEntity>
     */
    List<NamedEntity> getNamedEntities();

    /**
     * Get Token
     * @return List<Token>
     */
    List<Token> getTokens();

    /**
     * Get POS
     * @return List<POS>
     */
    List<POS> getPOS();

    /**
     * Get Sentiments
     * @return List<Sentiment>
     */
    List<Sentiment> getSentiments();


    /**
     * Get Document Sentiment
     * @return Sentiment
     */
    Sentiment getDocumentSentiment();

    /**
     * Get Lemmas
     * @return
     */
    List<Lemma> getLemmas();

    /**
     * Get Sentences
     * @return
     */
    List<Sentence> getSentences();

    /**
     * Is NLP Processed?
     * @return
     */
    boolean isProcessed();


}








