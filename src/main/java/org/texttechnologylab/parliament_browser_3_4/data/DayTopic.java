package org.texttechnologylab.parliament_browser_3_4.data;

import org.bson.Document;

import java.util.List;

public interface DayTopic {


    /**
     * Get the name of the Day Topic
     * @return the name as a String
     */
    String getName();

    /**
     * Set the name of the Day Topic
     * @param value : Name as a String
     */
    void setName(String value);

    /**
     * Get the title of the Day Topic
     * @return the title as a String
     */
    String getTitle();

    /**
     * Set the title of the Day Topic
     * @param title as a String.
     */
    void setTitle(String title);

    /**
     * Get the protocol of the Day Topic
     * @return Protocol
     */

    Protocol getProtocol();

    /**
     * Set the protocol of the Day Topic
     * @param value Protocol
     */
    void setProtocol(Protocol value);

    /**
     * Get a list of speeches that were mentioned in the Day Topic
     * @return list of Speeches
     */
    List<Speech> getSpeechList();

    /**
     * Add a Speech to the Day Topic.
     * @param value Speech
     */
    void addSpeech(Speech value);

    /**
     * Add many Speeches to the Day Topic
     * @param value a List of Speeches.
     */
    void addSpeeches(List<Speech> value);


    /**
     * Get the Factory of the Day Topic
     * @return Factory
     */
    Factory getFactory();

    /**
     * Converts the Day Topic into a MongoDB Document
     * @return a MongoDB Document.
     */
    Document toDocument();
}
