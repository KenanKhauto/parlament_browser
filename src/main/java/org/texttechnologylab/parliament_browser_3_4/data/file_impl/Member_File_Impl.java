package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.Factory;
import org.texttechnologylab.parliament_browser_3_4.data.Member;
import org.texttechnologylab.parliament_browser_3_4.data.Party;
import org.texttechnologylab.parliament_browser_3_4.helper.XMLHelper;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * A Class for parsing members of the german Bundestag.
 * @author Tim König
 */
public class Member_File_Impl extends Speaker_File_Impl implements Member {

    private String birthPlace = "";
    private String birthLand = "";
    private String sex = "";
    private String familyStatus = "";
    private String profession = "";
    private String vita = "";
    private String picUrl = "";
    private String picInfo = "";


    private Date birthDate = null;
    private Date deathDate = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Constructor for Member-Class
     * @param pNode XML-Node of member
     * @author Tim König
     */
    public Member_File_Impl(Factory pFactory, Node pNode) throws ParseException {
        super(pFactory);
        Node idNode = XMLHelper.getSingleNodesFromXML(pNode, "ID");
        if (idNode != null)
            this.setID(idNode.getTextContent());
        initialize(pNode);
    }

    /**
     * Default-constructor for Member-Class
     * @author Tim König
     */
    public Member_File_Impl() {

    }

    /**
     * @param pNode XML-Node of member
     * @author Tim König
     */
    private void initialize(Node pNode) throws ParseException {
        Node lastNameNode = XMLHelper.getSingleNodesFromXML(pNode, "NACHNAME");
        Node firstNameNode = XMLHelper.getSingleNodesFromXML(pNode, "VORNAME");
        Node titleNode = XMLHelper.getSingleNodesFromXML(pNode, "ANREDE_TITEL");
        Node partyNode = XMLHelper.getSingleNodesFromXML(pNode, "PARTEI_KURZ");
        Node birthDateNode = XMLHelper.getSingleNodesFromXML(pNode, "GEBURTSDATUM");
        Node birthPlaceNode = XMLHelper.getSingleNodesFromXML(pNode, "GEBURTSORT");
        Node birthLandNode = XMLHelper.getSingleNodesFromXML(pNode, "GEBURTSLAND");
        Node deathDateNode = XMLHelper.getSingleNodesFromXML(pNode, "STERBEDATUM");
        Node sexNode = XMLHelper.getSingleNodesFromXML(pNode, "GESCHLECHT");
        Node familyStatusNode = XMLHelper.getSingleNodesFromXML(pNode, "FAMILIENSTAND");
        Node professionNode = XMLHelper.getSingleNodesFromXML(pNode, "BERUF");
        Node vitaNode = XMLHelper.getSingleNodesFromXML(pNode, "VITA_KURZ");

        if (lastNameNode != null) this.setLastName(lastNameNode.getTextContent());
        if (firstNameNode != null) this.setFirstName(firstNameNode.getTextContent());
        if (titleNode != null) this.setTitle(titleNode.getTextContent());
        if (partyNode != null) {
            Party pParty = this.getFactory().getParty(partyNode.getTextContent());
            this.setParty(pParty);
        }
        if (birthDateNode != null)
            this.setBirthDate(new Date(this.dateFormat.parse(birthDateNode.getTextContent()).getTime()));
        if (birthPlaceNode != null) this.setBirthPlace(birthPlaceNode.getTextContent());
        if (birthLandNode != null) this.setBirthLand(birthLandNode.getTextContent());
        assert deathDateNode != null;
        if (!Objects.equals(deathDateNode.getTextContent(), ""))
            this.setDeathDate(new Date(this.dateFormat.parse(deathDateNode.getTextContent()).getTime()));
        if (sexNode != null) this.setSex(sexNode.getTextContent());
        if (familyStatusNode != null) this.setFamilyStatus(familyStatusNode.getTextContent());
        if (professionNode != null) this.setProfession(professionNode.getTextContent());
        if (vitaNode != null) this.setVita(vitaNode.getTextContent());
    }

    /**
     * get full name of member
     * @return full name as a String
     * @author Tim König
     */
    @Override
    public String getName() {
        if (!Objects.equals(this.getTitle(), ""))
            return this.getTitle() + " " + this.getFirstName() + " " + this.getLastName();
        else
            return this.getFirstName() + " " + this.getLastName();
    }

    /**
     * get birthdate (as Date) of member
     * @return birthdate as Date
     * @author Tim König
     */
    @Override
    public Date getBirthDate() {
        return this.birthDate;
    }

    /**
     * get birthdate (as formatted String) of member
     * @return birthdate as String
     * @author Tim König
     */
    public String getBirthDateFormatted() {
        if (this.getBirthDate() != null)
            return this.dateFormat.format((this.getBirthDate()));
        else return "";
    }

    /**
     * set birthdate of member
     * @param value birthdate as string
     * @author Tim König
     */
    @Override
    public void setBirthDate(Date value) {
        this.birthDate = value;
    }

    /**
     * get birthplace of member
     * @return birthplace as string
     * @author Tim König
     */
    @Override
    public String getBirthPlace() {
        return this.birthPlace;
    }

    /**
     * set birthplace of member
     * @param value birthplace as string
     * @author Tim König
     */
    @Override
    public void setBirthPlace(String value) {
        this.birthPlace = value;
    }

    /**
     * get country of birth of member
     * @return country of birth as string
     * @author Tim König
     */
    @Override
    public String getBirthLand() {
        return this.birthLand;
    }

    /**
     * set country of birth of member
     * @param value country of birth as string
     * @author Tim König
     */
    @Override
    public void setBirthLand(String value) {
        this.birthLand = value;
    }

    /**
     * get date of death of member
     * @return date of death as string
     * @author Tim König
     */
    @Override
    public Date getDeathDate() {
        return this.deathDate;
    }

    /**
     * get date of death (as formatted String) of member
     * @return date of death as String
     * @author Tim König
     */
    public String getDeathDateFormatted() {
        if (this.getDeathDate() != null)
            return this.dateFormat.format((this.getDeathDate()));
        else return "";
    }

    /**
     * set date of death (as Date) of member
     * @param value date of death as Date
     * @author Tim König
     */
    @Override
    public void setDeathDate(Date value) {
        this.deathDate = value;
    }

    /**
     * get sex of member
     * @return date of death as string
     * @author Tim König
     */
    @Override
    public String getSex() {
        return this.sex;
    }

    /**
     * set sex of member
     * @param value date of death as string
     * @author Tim König
     */
    @Override
    public void setSex(String value) {
        this.sex = value;
    }

    /**
     * get family status of member
     * @return date of death as string
     * @author Tim König
     */
    @Override
    public String getFamilyStatus() {
        return this.familyStatus;
    }

    /**
     * set family status of member
     * @param value date of death as string
     * @author Tim König
     */
    @Override
    public void setFamilyStatus(String value) {
        this.familyStatus = value;
    }

    /**
     * get profession of member
     * @return date of death as string
     * @author Tim König
     */
    @Override
    public String getProfession() {
        return this.profession;
    }

    /**
     * set profession of member
     * @param value date of death as string
     * @author Tim König
     */
    @Override
    public void setProfession(String value) {
        this.profession = value;
    }

    /**
     * get vita of member
     * @return date of death as string
     * @author Tim König
     */
    @Override
    public String getVita() {
        return this.vita;
    }

    /**
     * set vita of member
     * @param value date of death as string
     * @author Tim König
     */
    @Override
    public void setVita(String value) {
        this.vita = value;
    }


    /**
     * Get URL of members portrait picture
     * @return URL of picture (as String)
     * @author Tim König
     */
    public String getPicUrlString() {
        return this.picUrl;
    }

    /**
     * Set URL of members portrait picture
     * @param value URL of picture (as String)
     * @author Tim König
     */
    public void setPicUrlString(String value) {
        this.picUrl = value;
    }

    /**
     * Get Metadata of portrait picture
     * @return Metadate of picture (as String)
     * @author Tim König
     */
    public String getPicInfo() {
        return this.picInfo;
    }

    /**
     * Set Metadata of portrait picture
     * @param value metadata (as String)
     * @author Tim König
     */
    public void setPicInfo(String value) {
        this.picInfo = value;
    }

    /**
     * Converts Speaker into a MongoDB Document
     * @return a MongoDB Document
     * @author Tim König
     */
    @Override
    public Document toDocument() {
        Document doc = new Document();
        doc.append("_id", this.getID());
        doc.append("Name", this.getName());
        doc.append("First Name", this.getFirstName());
        doc.append("Last Name", this.getLastName());
        doc.append("Title", this.getTitle());
        doc.append("Party", this.getParty() != null ? this.getParty().getName() : "parteilos");
        switch (this.getParty().getName()) {
            case "CDU":
            case "CSU":
                doc.append("Faction", "CDU/CSU");
                break;
            case "SPD":
                doc.append("Faction", "SPD");
                break;
            case "FDP":
                doc.append("Faction", "FDP");
                break;
            case "GRÜNE":
                doc.append("Faction", "BÜNDNIS 90/DIE GRÜNEN");
                break;
            case "LINKE":
                doc.append("Faction", "DIE LINKE.");
                break;
            case "AfD":
                doc.append("Faction", "AfD");
                break;
            case "Bremen":
                doc.append("Faction", "Bremen");
                break;
            default:
                doc.append("Faction", "fraktionslos");
                break;
        }
        doc.append("Birth Date", this.getBirthDateFormatted());
        doc.append("Birth Place", this.getBirthPlace());
        doc.append("Birth Land", this.getBirthLand());
        doc.append("Death Date", this.getDeathDateFormatted());
        doc.append("Sex", this.getSex());
        doc.append("Family Status", this.getFamilyStatus());
        doc.append("Profession", this.getProfession());
        doc.append("Vita", this.getVita());
        doc.append("Picture Url", this.getPicUrlString());
        doc.append("Picture Info", this.getPicInfo());

        return doc;
    }
}
