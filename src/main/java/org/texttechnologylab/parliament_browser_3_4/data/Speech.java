package org.texttechnologylab.parliament_browser_3_4.data;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.exceptions.InputException;

import java.text.ParseException;
import java.util.List;

public interface Speech extends PlenaryObject {

    /**
     * get the speech text with the comments in it.
     * @return
     */
    String getTextWithComments();

    /**
     * get text content of the speech
     * @return text as a String
     */
    String getText();

    /**
     * set text content of the speech
     * @param value text as a String
     */
    void setText(String value);

    /**
     * get Speaker of the speech
     * @return Speaker
     */
    Speaker getSpeaker();

    /**
     * set Speaker of the speech
     * @param value Speaker
     */
    void setSpeaker(Speaker value);

    /**
     * get day topic of speech
     * @return DayTopic
     */
    DayTopic getDayTopic() throws ParseException;

    /**
     * set day topic if speech
     * @param value DayTopic
     */
    void setDayTopic(DayTopic value);

    /**
     * get protocol of speech
     * @return Protocol
     */
    Protocol getProtocol() throws ParseException;

    /**
     * set protocol of speech
     * @param value Protocol
     */
    void setProtocol(Protocol value);

    /**
     * get a list of comments that were mentioned in the speech
     * @return list of Comments
     */
    List<Comment> getComments();

    /**
     * add one Comment to comment list
     * @param value Comment
     */
    void addComment(Comment value);

    /**
     * add many comments to comment list
     * @param value list of comments
     */
    void addComments(List<Comment> value);


    /**
     * Converts speech to a MongoDB Document
     * @return a MongoDB Document
     */
    Document toDocumentNoNLP() throws InputException;

    /**
     * Converts speech to a MongoDB Document
     * @return a MongoDB Document
     */
    Document toDocumentNLP();

    /**
     * Converts speech to a JCas
     * @return a JCas
     */
    JCas toJCas() throws UIMAException;

    /**
     * Sets the JCas of the speech
     * @param jCas a JCas
     */
    void setJCas(JCas jCas);


}
