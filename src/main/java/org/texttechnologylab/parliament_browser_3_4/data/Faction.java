package org.texttechnologylab.parliament_browser_3_4.data;

import org.bson.Document;

import java.util.Set;

public interface Faction extends Comparable<Faction> {

    /**
     * Return the Name of the Faction
     * @return Name as a String
     */
    String getName();

    /**
     * Set the name of the Faction
     * @param sName Name as a String
     */
    void setName(String sName);

    /**
     * Add a Member of the Fraction
     * @param pSpeaker Speaker
     */
    void addMember(Speaker pSpeaker);

    /**
     * Add a Set of Members to the Faction
     * @param pSpeaker Set of Speakers
     */
    void addMembers(Set<Speaker> pSpeaker);

    /**
     * Get all Members of the Fraction
     * @return Set of Speakers
     */
    Set<Speaker> getMembers();


    /**
     * Converts Fraction to a MongoDB Document.
     * @return a MongoDB Document
     */
    Document toDocument();
}
