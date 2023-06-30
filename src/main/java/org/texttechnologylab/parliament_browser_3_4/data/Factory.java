package org.texttechnologylab.parliament_browser_3_4.data;

import org.w3c.dom.Node;

import java.util.Set;

public interface Factory {

    /**
     * get all speeches
     * @return
     */
    Set<Speech> getSpeeches();

    /**
     * get all speeches in a legislature period
     * @param legislaturPeriod
     * @return
     */
    Set<Speech> getSpeeches(int legislaturPeriod);

    /**
     * get all speeches in a protocol
     * @param protocol
     * @return
     */
    Set<Speech> getSpeeches(Protocol protocol);

    /**
     * get protocols in a legislatur period
     * @param period legislatur period as int
     * @return set of the protocols
     */
    Set<Protocol> getProtocolsInPeriod(int period);

    /**
     * Return all speakers
     * @return List of Speakers
     */
    Set<Speaker> getSpeakers();

    /**
     * Return all speakers by Fraction
     * @param pFaction a Fraction where the Speakers belong
     * @return a set of Speakers of a specific Fraction
     */
    Set<Speaker> getSpeakers(Faction pFaction);

    /**
     * Return all protocols
     * @return Set of protocols.
     */
    Set<Protocol> getProtocols();


    /**
     * get protocols in a legislatur period
     * @param lp legislatur period
     * @return protocol set
     */
    Set<Protocol> getProtocols(int lp);

    /**
     * Add a protocol to the protocol set.
     * @param pProtocol Protocol
     */
    void addProtocol(Protocol pProtocol);

    /**
     * Get all fractions
     * @return Set of fractions
     */
    Set<Faction> getFactions();

    /**
     * Get all parties
     * @return Set of Parties
     */
    Set<Party> getParties();

    /**
     * Get a specific Party by name. If the party does not exist, it will be created.
     * @param sName name of the Party
     * @return Party
     */
    Party getParty(String sName);

    /**
     * Get a speaker based on its Name. If the speaker does not exist, he / she will be created
     * @param sName name of the Speaker
     * @return Speaker
     */
    Speaker getSpeaker(String sName);


    /**
     * Add a speaker to speaker set
     * @param pSpeaker Speaker
     */
    void addSpeaker(Speaker pSpeaker);

    /**
     * Get a speaker based on a Node. If the speaker does not exist, he / she will be created
     * @param pNode XML-Node
     * @return Speaker
     */
    Speaker getSpeaker(Node pNode);

    /**
     * Get a fraction based on its Name. If the fraction does not exist, it will be created
     * @param sName Fraction name
     * @return Fraction
     */
    Faction getFaction(String sName);

    /**
     * Get a fraction based on a Node. If the fraction does not exist, it will be created
     * @param pNode XML-Node
     * @return Fraction
     */
    Faction getFaction(Node pNode);

}
