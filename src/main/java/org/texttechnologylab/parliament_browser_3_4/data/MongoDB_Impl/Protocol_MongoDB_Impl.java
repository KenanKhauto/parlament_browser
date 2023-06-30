package org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.DayTopic;
import org.texttechnologylab.parliament_browser_3_4.data.Protocol;
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Protocol_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.exceptions.InputException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Protocol_MongoDB_Impl extends Protocol_File_Impl implements Protocol {
    public Protocol_MongoDB_Impl() {

    }

    public Protocol_MongoDB_Impl(Document doc, Map<String, Speaker> speakerMap) {
        setID(doc.getString("_id"));
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        try {
            setDate(new java.sql.Date(sdfDate.parse(doc.getString("date")).getTime()));
            setBegin(new java.sql.Time(sdfTime.parse(doc.getString("begin")).getTime()));
            setEnd(new java.sql.Time(sdfTime.parse(doc.getString("end")).getTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        setDuration();
        setLegislaturePeriod(doc.getInteger("legislaturePeriod"));
        setNum(doc.getInteger("num"));
        setTitle(doc.getString("title"));
        setPlace(doc.getString("place"));
        List<DayTopic> dayTopics = new ArrayList<>();
        doc.getList("dayTopic", Document.class).forEach(dayTopicDoc -> {
            dayTopics.add(new DayTopic_MongoDB_Impl(dayTopicDoc, speakerMap));
        });
        setDayTopicList(dayTopics);
    }

    @Override
    public Document toDocument() throws InputException {
        Document doc = new Document();
        doc.append("_id", getID());
        doc.append("duration", getDuration());
        doc.append("date", getDateFormatted());
        doc.append("begin", getBeginFormatted());
        doc.append("end", getEndFormatted());
        doc.append("legislaturePeriod", getLegislaturePeriod());
        doc.append("num", getNum());
        doc.append("title", getTitle());
        List<Document> dayTopicDocuments = new ArrayList<>();
        getDayTopicList().forEach(dayTopic -> dayTopicDocuments.add(dayTopic.toDocument()));
        doc.append("dayTopic", dayTopicDocuments);
        doc.append("place", getPlace());

        return doc;
    }
}
