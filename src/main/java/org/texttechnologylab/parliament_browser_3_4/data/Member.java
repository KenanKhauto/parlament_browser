package org.texttechnologylab.parliament_browser_3_4.data;

import org.bson.Document;

import java.util.Date;

/**
 * Inteface for a Member of the german Bundestag.
 * @author Tim König
 */
public interface Member extends PlenaryObject {

    /**
     * get party of member
     * @return Party as Party-Object
     */
    Party getParty();

    /**
     * set party of member
     * @param party party as Party-Object
     */
    void setParty(Party party);

    /**
     * get full name of member
     * @return name as a String
     */
    String getName();

    /**
     * get first name of member
     * @return first name as a String
     */
    String getFirstName();

    /**
     * set first name
     * @param value first name as String
     */
    void setFirstName(String value);

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
     * get title of member
     * @return title as a String
     */
    String getTitle();

    /**
     * set title of member
     * @param value title as a String
     */
    void setTitle(String value);

    /**
     * get birthdate of member
     * @return birthdate as string
     */
    Date getBirthDate();

    /**
     * set birthdate of member
     * @param value birthdate as string
     */
    void setBirthDate(Date value);

    /**
     * Get formatted birthdate of member
     * @return Birthdate as String
     */
    String getBirthDateFormatted();

    /**
     * get birthplace of member
     * @return birthplace as string
     */
    String getBirthPlace();

    /**
     * set birthplace of member
     * @param value birthplace as string
     */
    void setBirthPlace(String value);

    /**
     * get country of birth of member
     * @return country of birth as string
     */
    String getBirthLand();

    /**
     * set country of birth of member
     * @param value country of birth as string
     */
    void setBirthLand(String value);

    /**
     * get date of death of member
     * @return date of death as string
     */
    Date getDeathDate();

    /**
     * Get formatted date of death of member
     * @return date of death as String
     */
    String getDeathDateFormatted();

    /**
     * set date of death of member
     * @param value date of death as string
     */
    void setDeathDate(Date value);

    /**
     * get sex of death of member
     * @return date of death as string
     */
    String getSex();

    /**
     * set sex of member
     * @param value date of death as string
     */
    void setSex(String value);

    /**
     * get family status of member
     * @return date of death as string
     */
    String getFamilyStatus();

    /**
     * set family status of member
     * @param value date of death as string
     */
    void setFamilyStatus(String value);

    /**
     * get profession of member
     * @return date of death as string
     */
    String getProfession();

    /**
     * set profession of member
     * @param value date of death as string
     */
    void setProfession(String value);

    /**
     * get vita of member
     * @return date of death as string
     */
    String getVita();

    /**
     * set vita of member
     * @param value date of death as string
     */
    void setVita(String value);

    /**
     * Get URL of members portrait picture
     * @return URL of picture (as String)
     */
    public String getPicUrlString();

    /**
     * Set URL of members portrait picture
     * @param value URL of picture (as String)
     */
    public void setPicUrlString(String value);

    /**
     * Get Metadata of portrait picture
     * @return Metadate of picture (as String)
     */
    public String getPicInfo();

    /**
     * Set Metadata of portrait picture
     * @param value metadata (as String)
     */
    public void setPicInfo(String value);

    /**
     * Converts Speaker into a MongoDB Document
     * @return a MongoDB Document
     */
    Document toDocument();
}
