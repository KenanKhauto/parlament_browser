package org.texttechnologylab.parliament_browser_3_4.data;

import org.bson.Document;

import java.util.Set;

public interface Party extends Comparable<Party> {
    /**
     * Return the name of the party
     * @return name as a String
     */
    String getName();

    /**
     * Set the name of the party
     * @param sValue name of the Party as String
     */
    void setName(String sValue);

    /**
     * Return all members of the party
     * @return set of speakers
     */
    Set<Speaker> getMembers();

    /**
     * Add a speaker as a member of the party
     * @param pMember Speaker
     */
    void addMember(Speaker pMember);

    /**
     * Add multiple speakers as members of the party
     * @param pSet set of Speakers
     */
    void addMembers(Set<Speaker> pSet);

    /**
     * Converts Party to a MongoDB Document
     * @return a MongoDB Document
     */

    Document toDocument();
}
