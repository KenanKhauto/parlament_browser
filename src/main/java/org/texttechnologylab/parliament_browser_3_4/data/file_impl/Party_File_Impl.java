package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.Party;
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to parse party from xml
 * @author kenan Khauto
 */

public class Party_File_Impl implements Party {

    private String sName = "";
    private final Set<Speaker> pMembers = new HashSet<>(0);

    /**
     * Default constructor
     */
    public Party_File_Impl() {
    }

    /**
     * Constructor based on the Name of a Party
     * @param sName party name
     */
    public Party_File_Impl(String sName) {
        this.setName(sName);
    }

    @Override
    public String getName() {
        return this.sName;
    }

    @Override
    public void setName(String sValue) {
        this.sName = sValue;
    }

    @Override
    public Set<Speaker> getMembers() {
        return this.pMembers;
    }

    @Override
    public void addMember(Speaker pMember) {
        this.pMembers.add(pMember);
    }

    @Override
    public void addMembers(Set<Speaker> pSet) {
        this.pMembers.addAll(pSet);
    }

    @Override
    public int compareTo(Party party) {
        return this.getName().compareTo(party.getName());
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        document.append("name", this.getName());

        List<String> memberIDs = new ArrayList<>();

        for (Speaker s : getMembers()) {
            memberIDs.add(s.getID());
        }

        document.append("memberIDs", memberIDs);

        return document;
    }
}
