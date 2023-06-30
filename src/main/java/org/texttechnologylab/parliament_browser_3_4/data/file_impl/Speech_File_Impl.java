package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.*;
import org.texttechnologylab.parliament_browser_3_4.helper.XMLHelper;
import org.texttechnologylab.parliament_browser_3_4.latex.template.SpeechLatexTemplate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


/**
 * A class for parsing speech from xml
 * @author kenan Khauto
 */
public class Speech_File_Impl extends PlenaryObject_File_Impl implements Speech {


    private Node speechNode;
    private String text = "";
    private Speaker speaker = null;
    private Protocol protocol;
    private DayTopic dayTopic;

    private String textWithComments = "";

    private List<Speech> insertions = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    /**
     * Constructor for Speech
     */
    public Speech_File_Impl() {

    }

    /**
     * Constructor
     * @param dayTopic
     * @param pNode
     */
    public Speech_File_Impl(DayTopic dayTopic, Node pNode) {
        super(dayTopic.getProtocol().getFactory());
        this.dayTopic = dayTopic;
        this.dayTopic.addSpeech(this);
        this.protocol = dayTopic.getProtocol();
        init(pNode);

    }

    /**
     * Constructor
     * @param dayTopic
     * @param sID
     */
    public Speech_File_Impl(DayTopic dayTopic, String sID) {
        super(dayTopic.getProtocol().getFactory());
        this.dayTopic = dayTopic;
        this.setID(sID);
        this.protocol = dayTopic.getProtocol();
    }

    /**
     * Initialization based on an XML-node
     * @param pNode
     */
    private void init(Node pNode) {
        this.setID(pNode.getAttributes().getNamedItem("id").getTextContent());

        // Source: Musterlösung with some updates
        // start
        NodeList nL = pNode.getChildNodes();

        Speaker currentSpeaker = null;
        Speech currentSpeech = this;
        int optionalSpeech = 1;
        int commentsCount = 1;
        int commentIndex = 0;

        for (int a = 0; a < nL.getLength(); a++) {
            Node n = nL.item(a);

            switch (n.getNodeName()) {

                case "p":

                    String sKlasse = "";
                    if (n.hasAttributes()) {
                        sKlasse = n.getAttributes().getNamedItem("klasse").getTextContent();
                    }

                    switch (sKlasse) {
                        case "redner":
                            Speaker nSpeaker = this.getFactory().getSpeaker(XMLHelper.getSingleNodesFromXML(n, "redner"));
                            currentSpeaker = nSpeaker;
                            currentSpeech = this;
                            nSpeaker.addSpeech(currentSpeech);
                            this.speaker = nSpeaker;


                            break;
                        case "n":

                            Speaker tSpeaker = this.getFactory().getSpeaker(n);
                            currentSpeaker = tSpeaker;
                            tSpeaker.addSpeech(currentSpeech);

                            break;

                        default:
                            currentSpeech.setText(n.getTextContent());
                            commentIndex = commentIndex + currentSpeech.getText().length();
                    }

                    break;

                case "name":

                    Speaker nSpeaker = this.getFactory().getSpeaker(n);

                    if (nSpeaker == this.getSpeaker()) {
                        currentSpeech = this;
                    } else {
                        if (currentSpeaker != nSpeaker && nSpeaker != null) {
                            currentSpeaker = nSpeaker;
                            currentSpeech = new Speech_File_Impl(getDayTopic(), getID() + "-" + optionalSpeech);
//                            currentSpeaker.addSpeech(currentSpeech);
//                            currentSpeech.setSpeaker(currentSpeaker);
//                            currentSpeech.setProtocol(this.dayTopic.getProtocol());
//                            currentSpeech.setDayTopic(this.dayTopic);
                            insertions.add(currentSpeech);
                            optionalSpeech++;
                        }
                    }

                    break;

                case "kommentar":
                    Comment_File_Impl pComment = new Comment_File_Impl(n, this);
                    this.setText(n.getTextContent());
                    pComment.setID("S-" + this.getID() + "-N" + commentsCount + "-P" + this.getProtocol().getNum());
                    commentsCount++;
                    this.addComment(pComment);
                    pComment.setIndex(commentIndex);
                    break;

            }

        }


        this.textWithComments = this.text;
        this.getComments().forEach(comment -> {
            this.text = this.text.replace(comment.getContent(), "");
        });

    }


    @Override
    public String getTextWithComments() {
        return this.textWithComments;
    }

    /**
     * @return
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @param value
     */

    @Override
    public void setText(String value) {
        this.text += value;
    }

    /**
     * @return
     */

    @Override
    public Speaker getSpeaker() {
        return this.speaker;
    }

    /**
     * @param value
     */
    @Override
    public void setSpeaker(Speaker value) {
        this.speaker = value;
    }

    /**
     * @return
     */
    @Override
    public DayTopic getDayTopic() {
        return this.dayTopic;
    }

    /**
     * @param value
     */
    @Override
    public void setDayTopic(DayTopic value) {
        this.dayTopic = value;
    }

    /**
     * @return
     */
    @Override
    public Protocol getProtocol() {
        return this.protocol;
    }


    /**
     * @param value
     */

    @Override
    public void setProtocol(Protocol value) {
        this.protocol = value;
    }


    /**
     * @return
     */

    @Override
    public List<Comment> getComments() {
        return this.comments;
    }

    /**
     * @param value
     */
    @Override
    public void addComment(Comment value) {
        this.comments.add(value);
    }

    /**
     * @param value
     */
    @Override
    public void addComments(List<Comment> value) {
        this.comments.addAll(value);
    }

    @Override
    public Document toDocumentNoNLP() {
        Document doc = new Document();
        doc.append("_id", getID());
        doc.append("SpeakerName", getSpeaker().getName());
        doc.append("SpeakerID", getSpeaker().getID());
        doc.append("Text", getText());
        doc.append("Party", getSpeaker().getParty() != null ? this.getSpeaker().getParty().getName() : "parteilos");
        doc.append("Faction", getSpeaker().getFaction() != null ? this.getSpeaker().getFaction().getName() : "fraktionslos");
        List<Document> commentDocuments = new ArrayList<>();
        getComments().forEach(comment -> commentDocuments.add(comment.toDocumentNoNLP()));
        doc.append("Comment", commentDocuments);
        return doc;
    }

    /**
     * Generates and returns the Latex Code for this Speech object
     * @return Latex code as String
     * @author Tim König
     */
    public String toTexString() {
        SpeechLatexTemplate speechLatexTmpl = new SpeechLatexTemplate(this);
        return speechLatexTmpl.getLatexOutput();
    }

    /**
     * returns SpeechLatexTemplate Object for this Speech Object
     * @return SpeechLatexTemplate Object
     * @author Tim König
     */
    public SpeechLatexTemplate toTexTemplate() {
        return new SpeechLatexTemplate(this);
    }

    @Override
    public Document toDocumentNLP() {
        return null;
    }

    public List<Speech> getInsertions() {
        return this.insertions;
    }

    @Override
    public JCas toJCas() throws UIMAException {
        return null;
    }

    @Override
    public void setJCas(JCas jCas) {

    }
}
