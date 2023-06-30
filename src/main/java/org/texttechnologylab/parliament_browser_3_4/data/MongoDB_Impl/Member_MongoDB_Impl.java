package org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl;

import org.bson.Document;
import org.texttechnologylab.parliament_browser_3_4.data.Member;
import org.texttechnologylab.parliament_browser_3_4.data.Party;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Member_File_Impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;


public class Member_MongoDB_Impl extends Member_File_Impl implements Member {

    public Member_MongoDB_Impl(Document doc, Map<String, Party> partyMap) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        setID(doc.getString("id"));
        setName(doc.getString("Name"));
        setLastName(doc.getString("Last Name"));
        setTitle(doc.getString("Title"));
        setParty(partyMap.get(doc.getString("Party")));
        try {
            setBirthDate(sdf.parse(doc.getString("Birth Date")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setBirthPlace(doc.getString("Birth Place"));
        setBirthLand(doc.getString("Birth Land"));
        try {
            if (doc.getString("Death Date") != null)
                setDeathDate(sdf.parse(doc.getString("Death Date")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setSex(doc.getString("Sex"));
        setFamilyStatus(doc.getString("Family Status"));
        setProfession(doc.getString("Profession"));
        setVita(doc.getString("Vita"));
    }
}
