package org.texttechnologylab.parliament_browser_3_4.data;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.exceptions.InputException;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Set;

public interface Protocol extends PlenaryObject {


    /**
     * get the Duration of the Protocol as a long
     * @return long Duration as seconds
     */
    long getDuration();

    /**
     * set the Duration
     * Difference between start and end.
     */
    void setDuration();

    /**
     * get date of protocol NOT formatted.
     * @return Date
     */
    Date getDate();

    /**
     * get the date formatted
     * @return date as a String
     */

    String getDateFormatted();


    /**
     * Set date of the protocol
     * @param pDate Date
     */
    void setDate(Date pDate);

    /**
     * get begin NOT formatted
     * @return Time
     */

    Time getBegin();

    /**
     * get begin of the protocol
     * @return String begin formatted
     * @throws InputException - if the to be parsed date not parsable the program will not stop
     */
    String getBeginFormatted() throws InputException;

    /**
     * set begin of protocol
     * @param value Time
     */
    void setBegin(Time value);

    /**
     * get end NOT formatted
     * @return Time
     */
    Time getEnd();

    /**
     * get end of the protocol
     * @return String end formatted
     * @throws InputException - if the to be parsed date not parsable the program will not stop
     */

    String getEndFormatted() throws InputException;

    /**
     * set end of protocol
     * @param value Time
     */
    void setEnd(Time value);

    /**
     * get Legislature Period of the protocol
     * @return int
     */
    int getLegislaturePeriod();

    /**
     * set Legislature Period of the protocol
     * @param value int
     */
    void setLegislaturePeriod(int value);

    /**
     * get the number of the protocol
     * @return int
     */
    int getNum();

    /**
     * set the number of the protocol
     * @param value int
     */
    void setNum(int value);

    /**
     * get title
     * @return String title
     */
    String getTitle();

    /**
     * set title
     * @param value String title
     */
    void setTitle(String value);


    /**
     * get a list of day topics
     * @return list of day topics
     */
    List<DayTopic> getDayTopicList();

    /**
     * add one day topic
     * @param value DayTopic
     */
    void addDayTopic(DayTopic value);

    /**
     * add many day topics
     * @param value list of day topics
     */
    void setDayTopicList(List<DayTopic> value);

    /**
     * get speakers of day topic
     * @return set of speakers
     */
    Set<Speaker> getSpeakers();

    /**
     * add one speaker to speaker set
     * @param value Speaker
     */
    void addSpeaker(Speaker value);

    /**
     * add many speakers to speaker set
     * @param value set of speakers
     */
    void addSpeakers(Set<Speaker> value);

    /**
     * get place of protocol
     * @return place as a String
     */
    String getPlace();

    /**
     * set place of protocol
     * @param value place as String
     */
    void setPlace(String value);

    /**
     * Converts protocol to MongoDB Document
     * @return a MongoDB Document
     * @throws InputException if input not parseable, won't stop
     */
    Document toDocument() throws InputException;
}
