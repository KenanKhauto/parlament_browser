package org.texttechnologylab.parliament_browser_3_4.data.file_impl;


import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.DayTopic;
import org.texttechnologylab.parliament_browser_3_4.data.Factory;
import org.texttechnologylab.parliament_browser_3_4.data.Protocol;
import org.texttechnologylab.parliament_browser_3_4.data.Speech;
import org.texttechnologylab.parliament_browser_3_4.exceptions.InputException;
import org.texttechnologylab.parliament_browser_3_4.helper.XMLHelper;
import org.texttechnologylab.parliament_browser_3_4.latex.template.DayTopicLatexTemplate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


/**
 * A class to parse the day topics from xml
 * @author kenan Khauto
 */
public class DayTopic_File_Impl extends PlenaryObject_File_Impl implements DayTopic {
    private String name = "";
    private String title = "";
    private Protocol protocol;
    private final List<Speech> speechList = new ArrayList<>();
    private Node dayTopicNode;

    /**
     * Constructor
     * @param protocol protocol of the day topic
     * @param node     DayTopic XML Node
     * @param num      a number to help giving id to day topic
     */
    public DayTopic_File_Impl(Protocol protocol, Node node, int num) {
        super(protocol.getFactory());
        this.dayTopicNode = node;
        this.protocol = protocol;
        this.setID("top-" + protocol.getNum() + "-" + num);

        init();
    }

    /**
     * Constructor
     */
    public DayTopic_File_Impl() {

    }

    void init() {

        setName(this.dayTopicNode.getAttributes().getNamedItem("top-id").getTextContent());

        List<Node> speechNodeList = XMLHelper.getNodesFromXML(this.dayTopicNode, "rede");

        for (Node node : speechNodeList) {
            Speech speech = new Speech_File_Impl(this, node);
            this.speechList.add(speech);
        }

        NodeList childNodes = this.dayTopicNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node.getNodeName().equals("p")) {
                String pClass = "";
                if (node.hasAttributes()) {
                    pClass = node.getAttributes().getNamedItem("klasse").getTextContent();
                }

                if (pClass.equals("T_NaS")) {
                    this.title += node.getTextContent();
                }
            }

        }

        this.title = this.title.trim();


    }

    @Override
    public String getID() {
        return super.getID();
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
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Protocol getProtocol() {
        return this.protocol;
    }

    @Override
    public void setProtocol(Protocol value) {
        this.protocol = value;
    }

    @Override
    public List<Speech> getSpeechList() {
        return this.speechList;
    }

    @Override
    public void addSpeech(Speech value) {
        this.speechList.add(value);
    }

    @Override
    public void addSpeeches(List<Speech> value) {
        this.speechList.addAll(value);
    }


    @Override
    public Factory getFactory() {
        return this.factory;
    }


    @Override
    public Document toDocument() {
        Document doc = new Document();
        doc.append("_id", getID());
        doc.append("name", getName());
        doc.append("title", getTitle());
        List<Document> speechDocuments = new ArrayList<>();
        getSpeechList().forEach(speech -> {
            try {
                speechDocuments.add(speech.toDocumentNoNLP());
            } catch (InputException e) {
                throw new RuntimeException(e);
            }
        });
        doc.append("speech", speechDocuments);
        return doc;
    }

    /**
     * Generates and returns the Latex Code for this DayTopic object
     * @return Latex code as String
     * @author Tim König
     */
    public String toTexString() {
        DayTopicLatexTemplate dayTopicLatexTmpl = new DayTopicLatexTemplate(this);
        return dayTopicLatexTmpl.getLatexOutput();
    }

    /**
     * returns DayTopicLatexTemplate Object for this Speech Object
     * @return DayTopicLatexTemplate Object
     */
    public DayTopicLatexTemplate toTexTemplate() {
        return new DayTopicLatexTemplate(this);
    }
}
