package org.texttechnologylab.parliament_browser_3_4.webscrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.Duration;
import java.util.*;


/**
 * Scrapping the Bundestag's Website to get the xml protocols of the 19th and 20th legislatur period files from the site
 * @author kenan Khauto
 */

public class WebScrapper {

    /**
     * Based on the membersUrlMap it scrapes a specific member url, based on a given name, for the picture
     * link of that member and returns a list of strings with the picture-URL and metadate of that picture.
     * @param nameMember name of a specific member
     * @param memberUrlMap map with URLs of active members
     * @return picture link and metadata as List of String
     * @author Tim König
     */
    public static List<String> getPicDataByName(String nameMember, Map<String, String> memberUrlMap) {
        List<String> returnList = new ArrayList<>(0);
        String picUrl = "";
        String metaData = "";
        String memberUrl = memberUrlMap.get(nameMember);
        try {
            if (!Objects.equals(memberUrl, "") && memberUrl != null) {
                org.jsoup.nodes.Document docSite = Jsoup.connect(memberUrl).get();
                Element imgElement = docSite.getElementsByClass("bt-bild-standard pull-left").get(0);
                picUrl = imgElement.getElementsByTag("img").get(0).absUrl("data-img-md-normal");
                if (imgElement.getElementsByTag("p").size() > 1)
                    metaData = imgElement.getElementsByTag("p").get(1).text();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        returnList.add(picUrl);
        returnList.add(metaData);
        return returnList;
    }

    /**
     * Saves Map wich contains name of member and link to their picture by scrapping it from their webbpage on
     * bundestag.de
     * @param membersUrlMapPath Path to the membersUrlMap file
     * @param picUrlMapPath path to the picUrlMap file
     * @author Tim König
     */
    public static void saveUrlMap(File membersUrlMapPath, File picUrlMapPath) {
        Map<String, String> membersUrlMap = WebScrapper.loadMap(membersUrlMapPath);
        Map<String, String> picUrlMap = WebScrapper.loadMap(picUrlMapPath);

        membersUrlMap.forEach((key, value) -> {
            if (!picUrlMap.containsKey(key)) {
                List<String> resultQuery = WebScrapper.getPicDataByName(key, membersUrlMap);
                picUrlMap.put(key, resultQuery.get(0) + "; " + resultQuery.get(1));
                System.out.println(key + ": " + picUrlMap.get(key));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        WebScrapper.saveMap(picUrlMapPath, picUrlMap);
    }

    /**
     * Loads a map file into a HashMap-datastructure
     * @param mapFile map-file as File
     * @return resulting HashMap
     * @author Tim König
     */
    public static Map<String, String> loadMap(File mapFile) {
        try {
            Properties properties = new Properties();
            FileReader readFile = new FileReader(mapFile);
            properties.load(readFile);
            Map<String, String> resultMap = new HashMap<>(0);
            for (String key : properties.stringPropertyNames()) {
                resultMap.put(key, properties.get(key).toString());
            }
            return resultMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves a hashmap in a specific file
     * @param file file to store the hashmap in
     * @param map hashmap to store
     * @author Tim König
     */
    public static void saveMap(File file, Map<String, String> map) {
        Properties properties = new Properties();
        properties.putAll(map);
        try {
            FileWriter ouputFile = new FileWriter(file);
            properties.store(ouputFile, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Srapes www.bundestag.de/abgeordnete for the list of the URLs tu every active member of the Bundestag and returns
     * a map with the membername as keys and their links as values
     * @return Map with urls of members
     * @author Tim König
     */
    public static Map<String, String> getMembersUrlMap() {

        Map<String, String> nameUrlMap = new HashMap<>(0);
        EdgeOptions browserOptions = new EdgeOptions();
        browserOptions.addArguments("--headless");
        WebDriver browser = new EdgeDriver();

        browser.get("https://www.bundestag.de/abgeordnete");
        WebElement listButton = browser.findElement(By.cssSelector("i.icon-list-bullet"));
        new Actions(browser).click(listButton).pause(Duration.ofSeconds(5)).perform();

        WebElement listNode = browser.findElement(By.className("bt-list-holder"));
        List<WebElement> listElements = listNode.findElements(By.cssSelector("li > a"));
        listElements.forEach(elem -> {
            String title = elem.getAttribute("title");
            String[] titleElments = title.split(" ");
            String name;
            if (titleElments.length == 3) name = titleElments[2] + " " + titleElments[0];
            else name = titleElments[1] + " " + titleElments[0].replace(",", "");
            String picUrl = elem.getAttribute("href");
            nameUrlMap.put(name, picUrl);
        });
        browser.quit();

        return nameUrlMap;
    }

    /**
     * This method gets all Protocols as a list of Dom Documents that has to be parsed by a Dom Parser
     * @return List<Document> the Protocols
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @author Kenan Khauto
     */

    public static List<Document> getProtocols() throws ParserConfigurationException, IOException, SAXException {

        String searchUrl = "https://www.bundestag.de";
        String searchUrl20 = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354?limit=10&noFilterSet=true";
        String searchUrl19 = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/543410-543410?limit=10&noFilterSet=true";

        List<org.jsoup.nodes.Document> allDocs = new ArrayList<>();

        int offset = 0;

        getProtocolsXML(searchUrl, searchUrl19, allDocs, offset);

        getProtocolsXML(searchUrl, searchUrl20, allDocs, offset);


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        List<Document> domDocs = new ArrayList<>();

        for (org.jsoup.nodes.Document jsoupDoc : allDocs) {
            String xmlString = jsoupDoc.html();
            Document domDoc = db.parse(new InputSource(new StringReader(xmlString)));
            domDocs.add(domDoc);
        }


        return domDocs;


    }

    /**
     * This method scraps the website and gets all the protocols as a list of Jsoup Documents
     * @param searchUrl  The Bundestag Url
     * @param requestURL The URL from the javascript request without the offset Parameter
     * @param allDocs    List of Jsoup Documents that has to be filled
     * @param offset     The starting offset
     * @throws IOException
     * @author Kenan Khauto
     */
    private static void getProtocolsXML(String searchUrl, String requestURL, List<org.jsoup.nodes.Document> allDocs, int offset) throws IOException {
        org.jsoup.nodes.Document doc;
        while (offset <= 500) {

            doc = Jsoup.connect(requestURL + "&offset=" + offset).get();
            doc.select("a.bt-link-dokument").forEach(a -> {
                try {
                    org.jsoup.nodes.Document d = Jsoup.connect(searchUrl + a.attr("href") +
                            "?api_key=GmEPb1B.bfqJLIhcGAsH9fTJevTglhFpCoZyAAAdhp").get();
                    allDocs.add(d);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            offset += 10;
        }
    }
}
