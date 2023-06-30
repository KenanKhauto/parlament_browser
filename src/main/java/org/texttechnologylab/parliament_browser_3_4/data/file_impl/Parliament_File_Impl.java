package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.texttechnologylab.parliament_browser_3_4.data.Factory;
import org.texttechnologylab.parliament_browser_3_4.data.Parliament;
import org.texttechnologylab.parliament_browser_3_4.data.Protocol;
import org.texttechnologylab.parliament_browser_3_4.webscrapper.WebScrapper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to manage reading and creating the factory and protocols
 * @author kenan Khauto
 */

public class Parliament_File_Impl implements Parliament {


    private List<Protocol> protocolList = new ArrayList<>();

    private Factory factory;

    /**
     * Constructor
     * @param pathToParty path to Stammdaten
     * @throws ParserConfigurationException:
     * @throws IOException:
     * @throws SAXException:
     */
    public Parliament_File_Impl(String pathToParty) throws ParserConfigurationException, IOException, SAXException {

        // creating the factory here
        factory = new Factory_File_Impl(pathToParty);


        List<Document> dProtocols = WebScrapper.getProtocols();

        dProtocols.forEach(dProtocol -> {
            Protocol protocol = null;
            try {
                protocol = new Protocol_File_Impl(factory, dProtocol);
            } catch (ParserConfigurationException | IOException | SAXException | ParseException e) {
                throw new RuntimeException(e);
            }
            protocol.setID(protocol.getID() + protocol.getNum() + "-" + protocol.getLegislaturePeriod());
            factory.addProtocol(protocol);
            protocolList.add(protocol);
        });


    }


    @Override
    public Factory getFactory() {
        return this.factory;
    }

    @Override
    public List<Protocol> getProtocols() {
        return this.protocolList;
    }

    @Override
    public void addProtocol(Protocol value) {
        this.protocolList.add(value);
    }

    @Override
    public void addProtocols(List<Protocol> value) {
        this.protocolList.addAll(value);
    }
}
