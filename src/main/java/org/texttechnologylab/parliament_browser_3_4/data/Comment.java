package org.texttechnologylab.parliament_browser_3_4.data;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.util.Set;

public interface Comment {


    String findCommentator(Set<Party> partySet, Set<Speaker> speakerSet);

    /**
     * get index of comment in a speech text
     * @return
     */
    int getIndex();

    /**
     * @param index
     */
    void setIndex(int index);

    /**
     * This method will set the index of the comment, where it was mentioned in
     * the speech
     * @param speechText a string of the speech text with the comments
     */
    void setIndex(String speechText);

    /**
     * @return Speech where the comment was mentioned
     */
    Speech getSpeech();


    /**
     * set the Speech of the comment
     * @param s Speech
     */
    void setSpeech(Speech s);

    /**
     * @return the text content of the comment as a String
     */
    String getContent();

    /**
     * Set the text content of the comment as a string
     * @param value String content
     */
    void setContent(String value);

    /**
     * @return Factory where everything is moderated
     */
    Factory getFactory();

    /**
     * @return the custom ID of the Comment as a String.
     */
    String getID();

    /**
     * Set the ID of the Comment as a String
     * @param value: ID as a string
     */

    void setID(String value);

    /**
     * Convert the Comment into a MongoDB Document
     * @return a MongoDB Document.
     */
    Document toDocumentNoNLP();

    /**
     * Convert the Comment into a MongoDB Document
     * @return a MongoDB Document.
     */
    Document toDocumentNLP();

    /**
     * Sets the JCas of the speech
     */
    JCas toJCas() throws UIMAException;

    /**
     * Sets the JCas of the speech
     * @param jCas a JCas
     */
    void setJCas(JCas jCas);
}
