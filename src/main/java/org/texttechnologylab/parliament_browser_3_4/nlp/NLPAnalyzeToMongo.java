package org.texttechnologylab.parliament_browser_3_4.nlp;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.CasIOUtils;
import org.bson.Document;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.texttechnologylab.parliament_browser_3_4.data.MongoDB_Impl.Speech_MongoDB_Impl;
import org.texttechnologylab.uimadb.UIMADatabaseInterface;
import org.texttechnologylab.uimadb.wrapper.mongo.MongoSerialization;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.CasSerializationException;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.SerializerInitializationException;
import org.texttechnologylab.uimadb.wrapper.mongo.serilization.exceptions.UnknownFactoryException;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for processing NLP documents with transfer to MongoDB.
 * @author Stanley Mathew
 */

public class NLPAnalyzeToMongo {


    public static NLPTool nlpTool = new NLPTool();


    /**
     * Central method for processing NLP documents with shift to MongoDB.
     * Transfer to JCas, analyze, serialize, evaluate JCas,
     * @param speechMongo
     * @return
     * @throws UIMAException
     * @throws IOException
     * @throws SAXException
     */
    public static Document toNLPDoc(Speech_MongoDB_Impl speechMongo) throws UIMAException, IOException, SAXException {

        URL ddcAdress = NLPAnalyzeToMongo.class.getClassLoader().getResource("ddc3.csv");

        JCas jCas = speechMongo.toJCas();
        jCas = nlpTool.analyze(jCas);

        System.out.println("serializing in process ...    \n");

        //  methode to serialize. Output: String "jCasAsString".
        String jCasAsString = serialize(jCas);


        speechMongo.getDoc().put("serialCas", jCasAsString);


        try {
            speechMongo.getDoc().put("PosValue", (new ArrayList<>(JCasUtil.select(jCas, Token.class)).stream().map(tok -> tok.getPos().getPosValue()).collect(Collectors.toList())));
            speechMongo.getDoc().put("CoarseValue", (new ArrayList<>(JCasUtil.select(jCas, Token.class)).stream().map(tok -> tok.getPos().getCoarseValue()).collect(Collectors.toList())));

            speechMongo.getDoc().put("ddcList", (new ArrayList<>(JCasUtil.select(jCas, CategoryCoveredTagged.class)).stream().map(anno -> anno.getValue()).collect(Collectors.toList())));
            speechMongo.getDoc().put("scoreList", (new ArrayList<>(JCasUtil.select(jCas, CategoryCoveredTagged.class)).stream().map(anno -> anno.getScore()).collect(Collectors.toList())));

            speechMongo.getDoc().put("lemma", toStringList(new ArrayList<>(speechMongo.getLemmas())));
            speechMongo.getDoc().put("pos", toStringList(new ArrayList<>(speechMongo.getPOS())));

            speechMongo.getDoc().put("token", toStringList(new ArrayList<>(JCasUtil.select(jCas, Token.class))));
            speechMongo.getDoc().put("sentences", toStringList(new ArrayList<>(JCasUtil.select(jCas, Sentence.class))));

            speechMongo.getDoc().put("entities", toStringList(new ArrayList<>(JCasUtil.select(jCas, NamedEntity.class))));
            speechMongo.getDoc().put("persons", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("PER")).collect(Collectors.toList())));
            speechMongo.getDoc().put("locations", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("LOC")).collect(Collectors.toList())));
            speechMongo.getDoc().put("organisations", toStringList(JCasUtil.select(jCas, NamedEntity.class).stream().filter(n -> n.getValue().equals("ORG")).collect(Collectors.toList())));
            if (speechMongo.getDocumentSentiment() != null) {
                speechMongo.getDoc().put("sentiment", speechMongo.getDocumentSentiment().getSentiment());
            } else {
                speechMongo.getDoc().put("sentiment", speechMongo.getSentiments().stream().mapToDouble(Sentiment::getSentiment).sum());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return speechMongo.getDoc();
    }


    /**
     * Helper Method transforms List of annotations to List of Strings
     * @param annoList
     * @return
     */
    public static List<String> toStringList(List<Annotation> annoList) {
        List<String> stringList = new ArrayList<>(0);
        annoList.forEach(anno -> {
            if (anno instanceof POS) {
                stringList.add(((POS) anno).getPosValue());
            } else {
                stringList.add(anno.getCoveredText());
            }
        });
        return stringList;
    }


    /**
     * Methode: Serialisierung eines CAS files zu einem XMI File
     * @param jCas
     * @return String
     * @throws IOException
     */
    public static String serializeJCasToXMI(JCas jCas) throws IOException {
        CAS cas = jCas.getCas();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CasIOUtils.save(cas, outputStream, SerialFormat.XMI);
        return outputStream.toString();
    }


    /**
     * Methode: Deserialisierung eines XMI files zu einem CAS
     * @param xmi
     * @return JCas
     * @throws IOException
     */
    @NotNull
    public static JCas deserializeJCasFromXMI(String xmi) throws IOException {
        JCas emptyCas = null;
        InputStream inputStream = new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8));
        CasIOUtils.load(inputStream, emptyCas.getCas());
        inputStream.close();
        return emptyCas;
    }

    /**
     * Methode: returns a serialized JCas as String
     * @param jCas
     * @return String
     * @throws IOException
     */
    public static String serialize(JCas jCas) throws UnknownFactoryException, SerializerInitializationException, CasSerializationException, JSONException {
        String serializedUima = null;
        serializedUima = MongoSerialization.serializeJCas(jCas);
        return serializedUima;
    }

    /**
     * Methode: deserializes a String to JCas
     * @param input
     * @return JCas
     * @throws IOException
     */
    public static JCas deserialize(String input) throws UIMAException, JSONException {
        JCas whatever = null;
        whatever = UIMADatabaseInterface.deserializeJCas(input);
        return whatever;
    }
}
