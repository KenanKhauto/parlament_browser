package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.*;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.util.Set;

/**
 * Comment Class
 * @author kenan Khauto
 */
public class Comment_File_Impl extends PlenaryObject_File_Impl implements Comment {

    private String content;
    private Speech speech;

    private String ID;
    private int index;

    public Comment_File_Impl() {

    }

    /**
     * Constructor
     * @param n      Comment XML node
     * @param speech Speech where the comment was mentioned
     */
    public Comment_File_Impl(Node n, Speech speech) {
        super(speech.getFactory());
        this.content = n.getTextContent();
        this.speech = speech;

    }


    @Override
    public String findCommentator(Set<Party> partySet, Set<Speaker> speakerSet) {


        for (Speaker s : speakerSet) {
            if (s.getName().length() > 0) {
                if (this.getContent().contains(s.getName().trim())) {
                    return s.getName().trim();
                }
            }

        }

        for (Party p : partySet) {
            if (this.getContent().contains(p.getName())) {
                return p.getName();
            }
        }


        return null;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void setIndex(String speechText) {
        if (speechText.contains(this.getContent())) {
            this.index = speechText.indexOf(this.getContent());
        } else {
            this.index = -1;
        }
    }

    @Override
    public Speech getSpeech() {
        return this.speech;
    }

    @Override
    public void setSpeech(Speech s) {
        this.speech = s;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public void setContent(String value) {
        this.content = value;
    }

    @Override
    public Factory getFactory() {
        return this.speech.getFactory();
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public void setID(String value) {
        this.ID = value;
    }


    @Override
    public Document toDocumentNoNLP() {
        Document document = new Document();

        document.append("_id", this.getID());
        document.append("content", this.getContent());
        document.append("speechID", this.getSpeech().getID());
        document.append("SpeakerID", findCommentator(this.getFactory().getParties(), this.getFactory().getSpeakers()));
        try {
            document.append("protocolID", "" + this.getSpeech().getProtocol().getNum());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return document;
    }

    @Override
    public Document toDocumentNLP() {
        return toDocumentNoNLP();
    }

    @Override
    public void setJCas(JCas jCas) {

    }

    @Override
    public JCas toJCas() throws UIMAException {
        return null;
    }
}
