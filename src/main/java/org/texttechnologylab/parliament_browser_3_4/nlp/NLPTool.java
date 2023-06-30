package org.texttechnologylab.parliament_browser_3_4.nlp;


import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.hucompute.textimager.fasttext.labelannotator.LabelAnnotatorDocker;
import org.hucompute.textimager.uima.gervader.GerVaderSentiment;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;


/**
 * Class introducing analysis-engine, pipeline creation with taggers and "analyze" method
 * @author Stanley Mathew
 */

public class NLPTool {

    // Variable: Object of AnalysisEngine for NLP processing.
    private AnalysisEngine analysisEng = null;

    /**
     * Constructor with AggregateBuilder and taggers
     */
    public NLPTool() {

        //  construction of pipeline introducing object of AggregateBuilder class
        AggregateBuilder pipeline = new AggregateBuilder();
        URL posmap = NLPTool.class.getClassLoader().getResource("am_posmap.txt");


        //  add required taggers to pipeline
        try {
            pipeline.add(createEngineDescription(SpaCyMultiTagger3.class,
                    SpaCyMultiTagger3.PARAM_REST_ENDPOINT, "http://spacy.lehre.texttechnologylab.org"

            ));
            pipeline.add(createEngineDescription(GerVaderSentiment.class,
                    GerVaderSentiment.PARAM_REST_ENDPOINT, "http://gervader.lehre.texttechnologylab.org",
                    GerVaderSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
            ));


            pipeline.add(createEngineDescription(LabelAnnotatorDocker.class,
                    LabelAnnotatorDocker.PARAM_FASTTEXT_K, 100,
                    LabelAnnotatorDocker.PARAM_CUTOFF, false,
                    LabelAnnotatorDocker.PARAM_SELECTION, "text",
                    LabelAnnotatorDocker.PARAM_TAGS, "ddc3",
                    LabelAnnotatorDocker.PARAM_USE_LEMMA, true,
                    LabelAnnotatorDocker.PARAM_ADD_POS, true,
                    LabelAnnotatorDocker.PARAM_POSMAP_LOCATION, posmap.getPath(),
                    LabelAnnotatorDocker.PARAM_REMOVE_FUNCTIONWORDS, true,
                    LabelAnnotatorDocker.PARAM_REMOVE_PUNCT, true,
                    LabelAnnotatorDocker.PARAM_REST_ENDPOINT, "http://ddc.lehre.texttechnologylab.org"));


            // creation of the actual AnalysisEngine using pipeline blocks
            this.analysisEng = pipeline.createAggregate();

        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }

    }  // end constructor


    /**
     * Methode to process jCas in pipeline
     * @param jCas
     * @return JCas
     * @throws UIMAException
     */
    public JCas analyze(JCas jCas) throws UIMAException {

        // run  pipeline
        try {
            SimplePipeline.runPipeline(jCas, this.analysisEng);
        } catch (UIMAException e) {
            e.printStackTrace();
        }
        return jCas;
    }


    /**
     * Methode to process several jCas in pipeline at once
     * @param jCasList
     * @return JCas
     * @throws UIMAException
     */
    public List<JCas> analyzeMany(List<JCas> jCasList) throws UIMAException {
        List<JCas> analyzeJCas = new ArrayList<>();
        jCasList.parallelStream().forEach(doc -> {
            try {
                analyzeJCas.add(analyze(doc));
            } catch (UIMAException e) {
                throw new RuntimeException(e);
            }
        });
        return analyzeJCas;
    }


    /**
     * Inspired by Musterlösung  2022
     * Helper Method to obtain suitable List of documents from annotations
     * @param annoList
     * @return
     */
    public static List<Document> annoToDocList(List<Annotation> annoList) {

        List<Document> docList = new ArrayList<>(0);

        annoList.forEach(anno -> {
            Document doc = new Document();
            doc.put("begin", anno.getBegin());                         // hier beliebig veränderbar, Max
            doc.put("end", anno.getEnd());
            if (anno instanceof Token) {
                doc.put("value", ((Token) anno).getPos().getCoveredText());      //  beliebig veränderbar
            }
            if (anno instanceof POS) {
                doc.put("value", ((POS) anno).getPosValue());          //  beliebig veränderbar
                doc.put("type", anno.getType().getShortName());
            } else if (anno instanceof CategoryCoveredTagged) {
                CategoryCoveredTagged pTemp = (CategoryCoveredTagged) anno;
                doc.put("value", pTemp.getValue());                    //  beliebig veränderbar
                doc.put("score", pTemp.getScore());
            } else if (anno instanceof Lemma) {
                Lemma temp = (Lemma) anno;
                doc.put("value", temp.getValue());
                POS p = JCasUtil.selectCovered(POS.class, anno).get(0);
                if (p != null) {
                    doc.put("pos", p.getType().getShortName());
                }
            } else if (anno instanceof org.hucompute.textimager.uima.type.GerVaderSentiment) {
                org.hucompute.textimager.uima.type.GerVaderSentiment temp = (org.hucompute.textimager.uima.type.GerVaderSentiment) anno;
                doc.put("value", temp.getSentiment());
                doc.put("subjectivity", temp.getSubjectivity());
                doc.put("positive", temp.getPos());
                doc.put("negative", temp.getNeg());
                doc.put("neutral", temp.getNeu());
            } else {
                doc.put("value", anno.getCoveredText());
            }
            docList.add(doc);
        });
        return docList;
    }


    /**
     * Helper Method to obtain suitable List of Strings from annotations
     * @param annoList
     * @return
     */
    public static List<String> annoToStringList(List<Annotation> annoList) {
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
}


