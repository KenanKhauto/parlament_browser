package org.texttechnologylab.parliament_browser_3_4.data;

import org.bson.Document;

import java.util.List;

public interface Speaker extends PlenaryObject {

    /**
     * get Party of speaker
     * @return Party
     */
    Party getParty();

    /**
     * set Party of speaker
     */
    void setParty();

    /**
     * set party of speaker
     * @param party Party
     */
    void setParty(Party party);

    /**
     * get fraction of speaker
     * @return Fraction
     */
    Faction getFaction();

    /**
     * set Fraction of speaker
     * @param value Fraction
     */

    void setFaction(Faction value);

    /**
     * get list of speeches
     * @return list of speeches
     */

    List<Speech> getSpeeches();

    /**
     * add many speeches
     * @param value list of speeches
     */
    void addSpeeches(List<Speech> value);

    /**
     * add one speech
     * @param value Speech
     */
    void addSpeech(Speech value);

    /**
     * set speeches
     * @param value list of speechIDs
     */
    void setSpeechID(List<String> value);

    /**
     * get speeches
     * @return list of speechIDs
     */
    List<String> getSpeechID();

    /**
     * get name of speaker
     * @return name as a String
     */
    String getName();

    /**
     * set name of speaker
     * @param value name as String
     */
    void setName(String value);


    /**
     * get first name of speaker
     * @return first name as a String
     */
    String getFirstName();

    /**
     * set first name
     * @param value first name as String
     */
    void setFirstName(String value);

    /**
     * get middle name of speaker
     * @return middle name as a String
     */
    String getMiddleName();

    /**
     * set middle name
     * @param value middle name as String
     */
    void setMiddleName(String value);

    /**
     * get last name
     * @return last name as a String
     */
    String getLastName();

    /**
     * set last name
     * @param value last name as a String
     */
    void setLastName(String value);

    /**
     * get role of speaker
     * @return role as a String
     */
    String getRole();

    /**
     * set role of speaker
     * @param value role as a String
     */
    void setRole(String value);

    /**
     * get title of speaker
     * @return title as a String
     */
    String getTitle();

    /**
     * set title of speaker
     * @param value title as a String
     */
    void setTitle(String value);


    /**
     * checks if the speaker is a leader by searching in name and role
     * @return true if leader and false if not
     */
    boolean isLeader();

    /**
     * checks if the speaker is form government by searching in name and role
     * @return true if government and false if not
     */
    boolean isGovernment();

    /**
     * Converts Speaker into a MongoDB Document
     * @return a MongoDB Document
     */
    Document toDocument();
}
