package org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.Faction;
import org.texttechnologylab.parliament_browser_3_4.data.Party;
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;
import org.texttechnologylab.parliament_browser_3_4.data.Speech;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Speaker_File_Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Speaker_MongoDB_Impl extends Speaker_File_Impl implements Speaker {
    public Speaker_MongoDB_Impl(Document doc, Map<String, Party> p, Map<String, Faction> f) {
        setID(doc.getString("_id"));
        setName(doc.getString("Name"));
        setFirstName(doc.getString("First Name"));
        setLastName(doc.getString("Last Name"));
        setTitle(doc.getString("Title"));
        setParty(p.get(doc.getString("Party")));
        setFaction(f.get(doc.getString("Faction")));
        setSpeechID(doc.getList("speeches", String.class));
    }

    public Speaker_MongoDB_Impl(Document doc, Map<String, Party> p) {
        setID(doc.getString("_id"));
        setLastName(doc.getString("Name"));
        setFirstName(doc.getString("First Name"));
        setLastName(doc.getString("Last Name"));
        setTitle(doc.getString("Title"));
        setParty(p.get(doc.getString("Party")));
    }

    public Speaker_MongoDB_Impl(Document doc) {
        setID(doc.getString("_id"));
        setLastName(doc.getString("Name"));
        setFirstName(doc.getString("First Name"));
        setLastName(doc.getString("Last Name"));
        setRole(doc.getString("role"));
        setTitle(doc.getString("Title"));
    }

    @Override
    public Document toDocument() {
        Document doc = new Document();
        doc.append("_id", getID());
        doc.append("name", getName());
        doc.append("firstName", getFirstName());
        doc.append("middleName", getMiddleName());
        doc.append("lastName", getLastName());
        doc.append("title", getTitle());
        doc.append("role", getRole());
        doc.append("party", getParty().getName());
        doc.append("faction", getFaction().getName());
        List<Speech> speeches = getSpeeches();
        List<String> speechIDs = new ArrayList<>();
        for (Speech speech : speeches) {
            speechIDs.add(speech.getID());
        }
        doc.append("speeches", speechIDs);
        return doc;
    }
}
