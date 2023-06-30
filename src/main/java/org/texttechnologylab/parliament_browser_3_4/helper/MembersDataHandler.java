package org.texttechnologylab.parliament_browser_3_4.helper;

import org.texttechnologylab.parliament_browser_3_4.data.Factory;
import org.texttechnologylab.parliament_browser_3_4.data.Member;
import org.texttechnologylab.parliament_browser_3_4.data.Speaker;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Factory_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.data.file_impl.Member_File_Impl;
import org.texttechnologylab.parliament_browser_3_4.webscrapper.WebScrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Class for handling the setup and parsing of the xml-file
 * containing information of members of the german Bundestag.
 * @author Tim König
 */
public class MembersDataHandler {

    private final Document document;
    private final Factory factory;
    private final Set<Member> membersSet = new HashSet<>(0);

    /**
     * Constructor for this class
     * @param pathMembers path to MDB_STAMMDATEN.XML as string
     */
    public MembersDataHandler(String pathMembers)
            throws ParserConfigurationException, IOException, SAXException, ParseException, URISyntaxException {

        this.factory = new Factory_File_Impl();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbFactory.newDocumentBuilder();
        this.document = db.parse(new File(pathMembers));
        this.initMembersList();
    }

    /**
     *
     */
    private void initMembersList() throws ParseException, URISyntaxException {
        URL fileUrl = this.getClass().getClassLoader().getResource("picUrlMap.txt");
        assert fileUrl != null;
        Map<String, String> picUrlMap = WebScrapper.loadMap(new File(fileUrl.toURI()));
        NodeList membersNodeList = this.document.getElementsByTagName("MDB");
        for (int i = 0; i < membersNodeList.getLength(); i++) {
            Node membersNode = membersNodeList.item(i);
            Member member = new Member_File_Impl(this.getFactory(), membersNode);
            String nameKey = member.getFirstName() + " " + member.getLastName();
            if (picUrlMap.containsKey(nameKey)) {
                String urlValue = picUrlMap.get(nameKey).split(";")[0];
                String urlInfo = picUrlMap.get(nameKey).split(";")[1];
                member.setPicUrlString(urlValue);
                member.setPicInfo(urlInfo);
            }
            this.getFactory().addSpeaker((Speaker) member);
            this.addMember(member);
        }
    }

    /**
     * Get factory of this class
     * @return factory
     */
    public Factory getFactory() {
        return this.factory;
    }

    /**
     * Get Members from parsed XML-File
     * @return Collection of members as Set
     */
    public Set<Member> getMembers() {
        return this.membersSet;
    }

    /**
     * @param pMember
     */
    public void addMember(Member pMember) {
        this.membersSet.add(pMember);
    }


}
