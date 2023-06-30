package org.texttechnologylab.parliament_browser_3_4.latex.template;

import org.texttechnologylab.parliament_browser_3_4.data.DayTopic;
import org.texttechnologylab.parliament_browser_3_4.data.Speech;
import org.texttechnologylab.parliament_browser_3_4.latex.LatexCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * Creates template and Latex output for instances of speech objects.
 * @author Tim König
 */
public class DayTopicLatexTemplate implements LatexTemplate {


    private static StringBuilder outputTemplate;
    private StringBuilder outputLatex;
    private DayTopic dayTopicSource;

    /**
     * Default-Constructor
     * @author Tim König
     */
    public DayTopicLatexTemplate() {
        this.outputLatex = new StringBuilder();
    }

    /**
     * Constructor with DayToipc object as parameter
     * @param agenda DayTopic object
     * @author Tim König
     */
    public DayTopicLatexTemplate(DayTopic agenda) {
        this.outputLatex = new StringBuilder();
        this.dayTopicSource = agenda;
        this.createLatexOutput();
    }

    /**
     * Initialize Latex template
     * @author Tim König
     */
    private static void initTemplate() {
        outputTemplate = new StringBuilder();
        LatexCommand nameSection = new LatexCommand("section", "<dayTopicName>");
        outputTemplate.append(nameSection).append("\n");
        LatexCommand titleSubSection = new LatexCommand("subsection", "Titel");
        outputTemplate.append(titleSubSection);
        outputTemplate.append("<dayTopicTitle>\n\n");
        outputTemplate.append("<speechesContent>");
    }

    /**
     * Creates latex code by replacing the template accordingly
     * @author Tim König
     */
    @Override
    public void createLatexOutput() {
        if (outputTemplate == null) initTemplate();
        this.outputLatex.setLength(0);
        StringBuilder speechesText = new StringBuilder();
        //create Latex code for every speech in daytopic as big string
        Set<Speech> speechesSet = new HashSet<>(this.dayTopicSource.getSpeechList());
        speechesSet.forEach(elem -> {
            SpeechLatexTemplate speechTemplate = new SpeechLatexTemplate(elem);
            speechesText.append(speechTemplate.getLatexOutput());
        });
        // create final latex output by replacing template variables with latex code
        this.outputLatex.append(outputTemplate.toString()
                .replace("<dayTopicName>", this.dayTopicSource.getName())
                .replace("<dayTopicTitle>", this.dayTopicSource.getTitle())
                .replace("<speechesContent>", speechesText));
    }

    /**
     * Getter-method for Latex code as String
     * @return Latex code as String
     * @author Tim König
     */
    @Override
    public String getLatexOutput() {
        return this.outputLatex.toString();
    }


    /**
     * Getter for Speech template
     * @return Template as String
     * @author Tim König
     */
    public static String getTemplate() {
        if (outputTemplate == null) initTemplate();
        return outputTemplate.toString();
    }

    /**
     * Updates content of template
     * @param string Content to update template (as String)
     * @author Tim König
     */
    public static void updateTemplate(String string) {
        if (outputTemplate == null) initTemplate();
        outputTemplate.setLength(0);
        outputTemplate.append(string);
    }
}
