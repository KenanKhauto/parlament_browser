package org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.Party;
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Party_File_Impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Party_MongoDB_Impl extends Party_File_Impl implements Party {
    public Party_MongoDB_Impl(Document doc, Map<String, Speaker> speakerMap) {
        setName(doc.getString("name"));
        addMembers(new HashSet<>(speakerMap.values()));
    }

    public Party_MongoDB_Impl(Document doc) {
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
