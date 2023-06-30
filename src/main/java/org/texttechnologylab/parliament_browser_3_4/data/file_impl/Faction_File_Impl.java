package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.Faction;
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Set;


/**
 * A Class to parse faction form xml
 * @author kenan Khauto
 */
public class Faction_File_Impl implements Faction {
    private String sName = "";
    private final Set<Speaker> pMembers = new HashSet<>(0);

    /**
     * default constructor
     */
    public Faction_File_Impl() {

    }

    /**
     * Constructor
     * @param pNode Faction XML Node
     */
    public Faction_File_Impl(Node pNode) {
        this.sName = pNode.getTextContent().trim();
    }

    @Override
    public void setName(String sName) {
        this.sName = sName;
    }

    @Override
    public String getName() {
        return this.sName;
    }

    @Override
    public void addMember(Speaker pSpeaker) {
        this.pMembers.add(pSpeaker);
    }

    @Override
    public void addMembers(Set<Speaker> pSpeaker) {
        this.pMembers.addAll(pSpeaker);
    }

    @Override
    public Set<Speaker> getMembers() {
        return this.pMembers;
    }

    /**
     * Override default compareTo method
     * @param faction
     * @return
     */
    @Override
    public int compareTo(Faction faction) {
        return this.getName().toLowerCase().compareTo(faction.getName().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return this.getName().toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }


    @Override
    public Document toDocument() {
        Document document = new Document();
        Set<String> memberIDs = new HashSet<>();
        for (Speaker s : this.getMembers()) {
            memberIDs.add(s.getID());
        }
        document.append("name", this.getName());
        document.append("memberIDs", memberIDs);
        return document;
    }
}
