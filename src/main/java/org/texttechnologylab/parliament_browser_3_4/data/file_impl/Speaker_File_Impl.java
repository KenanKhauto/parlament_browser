package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.*;
import org.texttechnologylab.parliament_browser_3_4.helper.XMLHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A class for parsing speakers
 * @author kenan Khauto
 */
public class Speaker_File_Impl extends PlenaryObject_File_Impl implements Speaker {


    private String name = "";
    private String firstName = "";
    private String middleName = "";
    private String lastName = "";
    private String role = "";
    private String title = "";
    private List<String> speechIDs = new ArrayList<>();
    private Faction faction;
    private Party party;

    private final List<Speech> speechList = new ArrayList<>();


    public Speaker_File_Impl(Factory pFactory) {
        super(pFactory);
    }

    /**
     * Constructor
     * @param pFactory
     * @param pNode
     */
    public Speaker_File_Impl(Factory pFactory, Node pNode) {
        super(pFactory);
        this.setID(pNode.getAttributes().getNamedItem("id").getTextContent());
        init(pNode);
    }

    public Speaker_File_Impl() {

    }

    /**
     * Internal init method based on XML-node
     * @param pNode
     */
    private void init(Node pNode) {

        Node nLastName = XMLHelper.getSingleNodesFromXML(pNode, "nachname");
        Node nFirstName = XMLHelper.getSingleNodesFromXML(pNode, "vorname");
        Node nNamensZusatz = XMLHelper.getSingleNodesFromXML(pNode, "namenszusatz");
        Node nTitle = XMLHelper.getSingleNodesFromXML(pNode, "titel");
        Node nRole = XMLHelper.getSingleNodesFromXML(pNode, "rolle_lang");
        Node nFraction = XMLHelper.getSingleNodesFromXML(pNode, "fraktion");


        if (nLastName != null) {
            this.setLastName(nLastName.getTextContent());
        }

        if (nFirstName != null) {
            this.setFirstName(nFirstName.getTextContent());
        }
        if (nNamensZusatz != null) {
            this.setLastName(nNamensZusatz.getTextContent() + " " + this.getLastName());
        }
        if (nTitle != null) {
            this.setTitle(nTitle.getTextContent());
        }
        if (nRole != null) {
            this.setRole(nRole.getTextContent());
        }
        if (nFraction != null) {
            this.setFaction(this.getFactory().getFaction(nFraction));
            this.getFaction().addMember(this);
        }

        this.setName((this.getFirstName().trim() + " " + this.getLastName().trim()));

        // clean up
        this.setFirstName(this.getFirstName().replaceAll(this.getRole(), ""));


        this.setParty();
    }

    @Override
    public Party getParty() {
        return this.party;
    }

    @Override
    public void setParty() {
        org.w3c.dom.Document partyDoc = ((Factory_File_Impl) this.getFactory()).getPartyDoc();
        NodeList partyNodes = partyDoc.getElementsByTagName("MDB");
        // Getting parties
        for (int i = 0; i < partyNodes.getLength(); i++) {
            Node mdbNode = partyNodes.item(i);
            String idFromMdb = XMLHelper.
                    getSingleNodesFromXML(mdbNode, "ID").getTextContent();

            if (idFromMdb != null) {
                if (idFromMdb.equals(this.getID())) {
                    Party party_ = this.getFactory().getParty(XMLHelper.getSingleNodesFromXML(
                            mdbNode,
                            "PARTEI_KURZ").getTextContent());
                    this.party = party_;
                    party.addMember(this);
                    break;
                }
            }
        }
    }

    @Override
    public void setParty(Party party) {
        this.party = party;
    }

    @Override
    public Faction getFaction() {
        return this.faction;
    }

    @Override
    public void setFaction(Faction value) {
        this.faction = value;
    }

    @Override
    public List<Speech> getSpeeches() {
        return this.speechList;
    }

    @Override
    public void addSpeeches(List<Speech> value) {
        this.speechList.addAll(value);
    }

    @Override
    public void addSpeech(Speech value) {
        this.speechList.add(value);
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public void setFirstName(String value) {
        this.firstName = value;
    }

    @Override
    public String getMiddleName() {
        return this.middleName;
    }

    @Override
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public void setLastName(String value) {
        this.lastName = value;
    }

    @Override
    public String getRole() {
        return this.role;
    }

    @Override
    public void setRole(String value) {
        this.role = value;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String value) {
        this.title = value;
    }

    @Override
    public boolean isLeader() {
        boolean rBool = false;

        rBool = this.getRole().startsWith("Präsident") || this.getRole().startsWith("Vizepräsident") || this.getRole().startsWith("Alters");

        if (!rBool) {
            rBool = this.getName().startsWith("Präsident") || this.getName().startsWith("Vizepräsident");
        }

        return rBool;
    }

    @Override
    public boolean isGovernment() {

        boolean rBool = this.getRole().contains("minister");

        if (this.getRole().contains("kanzler")) {
            rBool = true;
        }

        if (this.getRole().contains("Staat")) {
            rBool = false;
        }

        return rBool;

    }

    @Override
    public void setSpeechID(List<String> speechIDs){
        this.speechIDs = speechIDs;
    }

    @Override
    public List<String> getSpeechID(){
        return this.speechIDs;
    }


    /**
     * Creating a Bson Document from the instance
     * @return Document
     */


    @Override
    public Document toDocument() {
        Document document = new Document();

//        String id_;
//        if (!this.getID().equals("")) {
//
//            id_ = this.getID();
//        }
//        else{
//            id_ = this.getName();
//        }

        Set<String> speechIDsSet = new HashSet<>();

        for (Speech s : this.getSpeeches()) {
            speechIDsSet.add(s.getID());
        }

        document.append("_id", this.getID());
        document.append("Name", this.getName());
        document.append("First Name", this.getFirstName());
        document.append("Last Name", this.getLastName());
        document.append("Role", this.getRole());
        document.append("Title", this.getTitle());
        document.append("Fraction", this.getFaction() != null ? this.getFaction().getName() : "fraktionslos");
        document.append("Party", this.getParty() != null ? this.getParty().getName() : "parteilos");
        document.append("Speeches_IDs", speechIDsSet);
        document.append("isLeader", this.isLeader());
        document.append("isGouvernment", this.isGovernment());
        return document;


    }
}
