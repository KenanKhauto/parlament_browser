package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.texttechnologylab.parliament_browser_3_4.data.*;
import org.texttechnologylab.parliament_browser_3_4.exceptions.InputException;
import org.texttechnologylab.parliament_browser_3_4.helper.XMLHelper;
import org.texttechnologylab.parliament_browser_3_4.latex.template.ProtocolLatexTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A class for parsing protocol
 * @author kenan Khauto
 */
public class Protocol_File_Impl extends PlenaryObject_File_Impl implements Protocol {

    private Document document;
    private List<DayTopic> dayTopicSet = new ArrayList<>();
    private String place;
    private Time beginn;
    private Time end;
    private Date date;
    private int num;
    private int legislaturePeriod;
    private String title;

    private long duration;
    private final Set<Speaker> speakerSet = new HashSet<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");


    /**
     * Constructor
     * @param factory   : Factory
     * @param dProtocol : Document as a Dom Document
     */
    public Protocol_File_Impl(Factory factory, Document dProtocol) throws ParserConfigurationException, IOException, SAXException, ParseException {
        super(factory);
        this.document = dProtocol;

        this.init();
    }

    public Protocol_File_Impl() {

    }

    /**
     * extract info from XML and set attributes
     */

    private void init() throws ParseException {

        // <<<<<<< INSPIRED BY THE MUSTERLÖSUNG >>>>>>>>


        // Define Date and Time Format
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

        this.setLegislaturePeriod(Integer.parseInt(getNodeFromXML(this.document, "wahlperiode").getTextContent().trim()));
        this.setNum(Integer.parseInt(getNodeFromXML(this.document, "sitzungsnr").getTextContent().trim()));
        this.setTitle(getNodeFromXML(this.document, "sitzungstitel").getTextContent().trim().replace("\n", "")
                .replaceAll(" ", ""));

        Node nDatum = getNodeFromXML(this.document, "datum");
        Date pDate = new Date(sdfDate.parse(nDatum.getAttributes().getNamedItem("date").getTextContent()).getTime());
        this.setDate(pDate);

        setPlace(XMLHelper.getSingleNodesFromXML(this.document.getElementsByTagName("kopfdaten").item(0), "ort")
                .getTextContent().replaceAll("\n", "").trim());


        // extract the start of a session
        Node nStart = getNodeFromXML(this.document, "sitzungsbeginn");
        Time pStartTime = null;
        String sStartTime = nStart.getAttributes().getNamedItem("sitzung-start-uhrzeit").getTextContent();
        sStartTime = sStartTime.replaceAll("\\.", ":");
        sStartTime = sStartTime.replace(" Uhr", "");
        try {
            pStartTime = new Time(sdfTime.parse(sStartTime).getTime());
        } catch (ParseException pe) {
            pStartTime = new Time(sdfTime.parse(nStart.getAttributes().getNamedItem("sitzung-start-uhrzeit").getTextContent().replaceAll("\\.", ":")).getTime());
        }
        this.setBegin(pStartTime);

        // extract the end of a session

        String sEndTime;
        Node nEnde;
        Time pEndTime = null;

        nEnde = this.document.getElementsByTagName("dbtplenarprotokoll").item(0);
        sEndTime = nEnde.getAttributes().getNamedItem("sitzung-ende-uhrzeit").getTextContent();
        sEndTime = sEndTime.replaceAll("\\.", ":");
        sEndTime = sEndTime.replace(" Uhr", "");
        try {
            pEndTime = new Time(sdfTime.parse(sEndTime).getTime());
        } catch (Exception pe) {
            try {
                pEndTime = new Time(sdfTime.parse(nEnde.getAttributes().getNamedItem("sitzung-ende-uhrzeit")
                        .getTextContent().replaceAll("\\.", ":")).getTime());
            } catch (ParseException peFinal) {
                System.err.println(peFinal.getMessage());
            }
        }


//        try {
//            pEndTime = new Time(sdfTime.parse(sEndTime).getTime());
//        }
//        catch (Exception pe){
//            try {
//                pEndTime = new Time(sdfTime.parse(nEnde.getAttributes().getNamedItem("sitzung-ende-uhrzeit")
//                        .getTextContent().replaceAll("\\.", ":")).getTime());
//            }
//            catch (ParseException peFinal){
//                System.err.println(peFinal.getMessage());
//            }
//        }
        this.setEnd(pEndTime);
        this.setDuration();
        this.setDayTopicList();
    }


    /**
     * Creating DayTopics and add them to dayTopicList
     */
    public void setDayTopicList() {
//        NodeList pBlocks = this.document.getElementsByTagName("ivz-block");
        NodeList topTags = this.document.getElementsByTagName("tagesordnungspunkt");

        for (int b = 0; b < topTags.getLength(); b++) {
            Node n = topTags.item(b);

            DayTopic dt = new DayTopic_File_Impl(this, n, b);
            this.addDayTopic(dt);

//            if(dt.getSpeechList().size()>0){
//                this.addDayTopic(dt);
//            }
        }
    }

    public Document getDocument() {
        return this.document;
    }


    /**
     * Extract a Node by a tag-name
     * @param pDocument : Document
     * @param sTag      : String
     * @return Node
     */
    private Node getNodeFromXML(Document pDocument, String sTag) {
        return pDocument.getElementsByTagName(sTag).item(0);
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public void setDuration() {
        if (this.getBegin() != null && this.getEnd() != null) {

            this.duration = this.getEnd().getTime() - this.getBegin().getTime();
        } else {
            this.duration = 0;
        }
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    @Override
    public String getDateFormatted() {
        return dateFormat.format(getDate());
    }

    @Override
    public void setDate(Date pDate) {
        this.date = pDate;
    }

    @Override
    public Time getBegin() {
        return this.beginn;
    }

    @Override
    public String getBeginFormatted() throws InputException {
        if (getBegin() != null) {
            return timeFormat.format(getBegin()) + " Uhr";
        }
        throw new InputException("not valid time");
    }


    @Override
    public void setBegin(Time value) {
        this.beginn = value;
    }

    @Override
    public Time getEnd() {
        return this.end;
    }

    @Override
    public String getEndFormatted() throws InputException {
        if (getEnd() != null) {
            return timeFormat.format(getEnd()) + " Uhr";
        }
        throw new InputException("not valid time");
    }

    @Override
    public void setEnd(Time value) {
        this.end = value;
    }

    @Override
    public int getLegislaturePeriod() {
        return this.legislaturePeriod;
    }

    @Override
    public void setLegislaturePeriod(int value) {
        this.legislaturePeriod = value;
    }

    @Override
    public int getNum() {
        return this.num;
    }

    @Override
    public void setNum(int value) {
        this.num = value;
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
    public List<DayTopic> getDayTopicList() {
        return this.dayTopicSet;
    }

    @Override
    public void addDayTopic(DayTopic value) {
        this.dayTopicSet.add(value);
    }

    @Override
    public void setDayTopicList(List<DayTopic> value) {
        this.dayTopicSet = value;
    }

    @Override
    public Set<Speaker> getSpeakers() {
        return this.speakerSet;
    }

    @Override
    public void addSpeaker(Speaker value) {
        this.speakerSet.add(value);
    }

    @Override
    public void addSpeakers(Set<Speaker> value) {
        this.speakerSet.addAll(value);
    }


    @Override
    public String getPlace() {
        return this.place;
    }

    @Override
    public void setPlace(String value) {
        this.place = value;
    }

    @Override
    public String toString() {
        return this.getNum() + "\t" + this.getDateFormatted() + "\t wp:" + this.getLegislaturePeriod();
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    /**
     * Special hashCode method
     * @return int
     */
    @Override
    public int hashCode() {
        return (int) this.getDate().getTime();
    }

    /**
     * Special compareTo method
     * @param plenaryObject : PlenaryObject
     * @return int
     */
    @Override
    public int compareTo(PlenaryObject plenaryObject) {
        if (plenaryObject instanceof Protocol) {
            return ((Protocol) plenaryObject).getDate().compareTo(this.getDate());
        }
        return super.compareTo(plenaryObject);
    }


    @Override
    public org.bson.Document toDocument() throws InputException {
        org.bson.Document doc = new org.bson.Document();
        doc.append("_id", getID());
        doc.append("duration", getDuration());
        doc.append("date", getDateFormatted());
        doc.append("begin", getBeginFormatted());
        doc.append("end", getEndFormatted());
        doc.append("legislaturePeriod", getLegislaturePeriod());
        doc.append("num", getNum());
        doc.append("title", getTitle());
        List<org.bson.Document> dayTopicDocuments = new ArrayList<>();
        getDayTopicList().forEach(dayTopic -> dayTopicDocuments.add(dayTopic.toDocument()));
        doc.append("dayTopic", dayTopicDocuments);
        doc.append("place", getPlace());

        return doc;
    }

    /**
     * Generates and returns the Latex Code for this Protocol object
     * @return Latex code as String
     * @author Tim König
     */
    public String toTexString() {
        ProtocolLatexTemplate protocolLatexTmpl = new ProtocolLatexTemplate(this);
        return protocolLatexTmpl.getLatexOutput();
    }

    /**
     * returns ProtocolLatexTemplate Object for this Speech Object
     * @return ProtocolLatexTemplate Object
     * @author Tim König
     */
    public ProtocolLatexTemplate toTexTemplate() {
        return new ProtocolLatexTemplate(this);
    }

}
