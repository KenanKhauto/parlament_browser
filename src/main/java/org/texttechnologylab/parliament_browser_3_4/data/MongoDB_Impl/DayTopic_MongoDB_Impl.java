package org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.DayTopic;
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;
import org.texttechnologylab.parliament_browser_3_4.data.Speech;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.DayTopic_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.exceptions.InputException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DayTopic_MongoDB_Impl extends DayTopic_File_Impl implements DayTopic {
    public DayTopic_MongoDB_Impl(Document doc, Map<String, Speaker> speakerMap) {
        setID(doc.getString("_id"));
        setName(doc.getString("name"));
        setTitle(doc.getString("title"));
        List<Speech> speeches = new ArrayList<>();
        if (doc.getList("speech", Document.class) != null) {
            doc.getList("speech", Document.class).forEach(speechDoc -> {
                speeches.add(new Speech_MongoDB_Impl(speechDoc, speakerMap));
            });
        }
        addSpeeches(speeches);
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
}

