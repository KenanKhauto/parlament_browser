package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.texttechnologylab.parliament_browser_3_4.data.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Central management system
 * @author kenan Khauto
 */

public class Factory_File_Impl implements Factory {

    private final Set<Speaker> pSpeaker = new HashSet<>();
    private final Set<Protocol> pProtocols = new HashSet<>();
    private final Set<Faction> pFactions = new HashSet<>();
    private final Set<Party> pParties = new HashSet<>();
    private Document partyDoc;

    /**
     * Constructor
     * @param pathToPar the path to STAMMDATEN to help getting missing information like Party
     */
    public Factory_File_Impl(String pathToPar) throws ParserConfigurationException,
            IOException, SAXException {

        File file = new File(pathToPar + "/" + "MDB_STAMMDATEN.XML");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        this.partyDoc = db.parse(file);
    }

    /**
     * Default-Constructor
     */
    public Factory_File_Impl() {
    }

    public Document getPartyDoc() {
        return this.partyDoc;
    }

    @Override
    public Set<Speech> getSpeeches() {

        Set<Speech> speechSet = new HashSet<>();

        this.getProtocols().forEach(pr -> {
            pr.getDayTopicList().forEach(dt -> {
                speechSet.addAll(dt.getSpeechList());
            });
        });

        return speechSet;
    }


    @Override
    public Set<Speech> getSpeeches(int legislaturPeriod) {
        return getSpeeches().stream().filter(s -> {
            try {
                return s.getProtocol().getLegislaturePeriod() == legislaturPeriod;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<Speech> getSpeeches(Protocol protocol) {
        Set<Protocol> protocols = this.getProtocols().stream().filter(p -> p == protocol).collect(Collectors.toSet());

        if (protocols.size() == 1) {
            return getSpeeches().stream().filter(s -> {
                try {
                    return s.getProtocol().equals(protocols.stream().findFirst().get());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toSet());
        }

        return null;
    }

    @Override
    public Set<Protocol> getProtocolsInPeriod(int period) {
        return this.getProtocols().stream().filter(p -> p.getLegislaturePeriod() == period).collect(Collectors.toSet());
    }

    @Override
    public Set<Speaker> getSpeakers() {
        return this.pSpeaker;
    }


    @Override
    public Set<Speaker> getSpeakers(Faction pFaction) {


        return this.getSpeakers().stream().filter(s -> {
            if (s.getFaction() != null) {
                return s.getFaction().equals(pFaction);
            }
            return false;
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<Protocol> getProtocols() {
        return this.pProtocols;
    }

    @Override
    public Set<Protocol> getProtocols(int lp) {
        return getProtocols().stream().filter(p -> p.getLegislaturePeriod() == lp).collect(Collectors.toSet());
    }

    @Override
    public void addProtocol(Protocol pProtocol) {
        this.pProtocols.add(pProtocol);
    }

    @Override
    public Set<Faction> getFactions() {
        return pFactions;
    }

    @Override
    public Set<Party> getParties() {
        return this.pParties;
    }

    @Override
    public Party getParty(String pName) {

        // search in parties and if not found create one and return

        List<Party> partyList = new ArrayList<>();

        for (Party p : this.getParties()) {
            if (p.getName().equals(pName)) {
                partyList.add(p);
            }
        }
        if (partyList.size() == 1) {
            //System.out.println("Party found!");
            return partyList.get(0);
        } else {
            Party pParty = new Party_File_Impl(pName);
            this.pParties.add(pParty);
            //System.out.println("Party created!");
            return pParty;
        }

    }

    @Override
    public Speaker getSpeaker(String sId) {


        List<Speaker> sList = this.getSpeakers().stream().filter(s -> s.getID().equals(sId)).collect(Collectors.toList());

        if (sList.size() == 1) {
            return sList.get(0);
        }

        return null;

    }

    @Override
    public void addSpeaker(Speaker pSpeaker) {
        this.pSpeaker.add(pSpeaker);
    }

    public Speaker getSpeakerByName(String sValue) {

        List<Speaker> sList = this.getSpeakers().stream().filter(s -> {
            return s.getName().replaceAll(" ", "").equalsIgnoreCase(Speaker_Plain_File_Impl.transform(sValue).replaceAll(" ", ""));
        }).collect(Collectors.toList());

        if (sList.size() == 1) {
            return sList.get(0);
        }
        return null;

    }

    @Override
    public Speaker getSpeaker(Node pNode) {

        Speaker pSpeaker = null;

        // <<<< Source: Musterlösung >>>>>
        // start

        // if speaker is a complex node
        if (!pNode.getNodeName().equalsIgnoreCase("name")) {
            String sID = pNode.getAttributes().getNamedItem("id").getTextContent();

            pSpeaker = getSpeaker(sID);

            if (pSpeaker == null) {
                Speaker_File_Impl nSpeaker = new Speaker_File_Impl(this, pNode);
                this.pSpeaker.add(nSpeaker);
                pSpeaker = nSpeaker;
            }
        }
        // if not...
        else {
            pSpeaker = getSpeakerByName(pNode.getTextContent());

            if (pSpeaker == null) {
                Speaker_Plain_File_Impl plainSpeaker = new Speaker_Plain_File_Impl(this, pNode);
                this.pSpeaker.add(plainSpeaker);
                pSpeaker = plainSpeaker;
            }

        }

        // End

        return pSpeaker;
    }

    @Override
    public Faction getFaction(String sName) {

        // <<<< This trick was taken from the Musterlösung! >>>>

        /*
         * search in factions if there is a faction with this name?
         * Attention: Since in Bündnis 90/Die Grünen partly other characters are used, here a small trick is used and
         * not checked for the simultaneity of the name of the faction but only for their same beginning.
         */
        List<Faction> sList = this.getFactions().stream().filter(s -> {
            if (s.getName().startsWith(sName.substring(0, 3))) {
                return true;
            }
            return s.getName().equalsIgnoreCase(sName.trim());
        }).collect(Collectors.toList());

        if (sList.size() == 1) {
            return sList.get(0);
        }

        return null;
    }

    @Override
    public Faction getFaction(Node pNode) {
        String sName = pNode.getTextContent();

        Faction pFaction = getFaction(sName);

        if (pFaction == null) {
            // if faction not exist, create
            pFaction = new Faction_File_Impl(pNode);
            this.pFactions.add(pFaction);
        }

        return pFaction;
    }

}
