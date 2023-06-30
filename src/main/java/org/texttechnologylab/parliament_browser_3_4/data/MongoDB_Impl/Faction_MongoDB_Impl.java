package org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.Faction;
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Faction_File_Impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Faction_MongoDB_Impl extends Faction_File_Impl implements Faction {
    public Faction_MongoDB_Impl(Document doc, Map<String, Speaker> s) {
        setName(doc.getString("name"));
        addMembers(new HashSet<>(s.values()));
    }

    public Faction_MongoDB_Impl(Document doc) {
        setName(doc.getString("name"));
    }

    @Override
    public Document toDocument() {
        Document doc = new Document();
        doc.append("name", getName());
        List<String> memberIDs = new ArrayList<>();
        getMembers().forEach(speaker -> memberIDs.add(speaker.getID()));
        doc.append("memberIDs", memberIDs);
        return doc;
    }
}

