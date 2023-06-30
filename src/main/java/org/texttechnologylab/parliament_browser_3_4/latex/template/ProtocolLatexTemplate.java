package org.texttechnologylab.parliament_browser_3_4.latex.template;

import org.texttechnologylab.parliament_browser_3_4.data.DayTopic;
import org.texttechnologylab.parliament_browser_3_4.data.Protocol;
import org.texttechnologylab.parliament_browser_3_4.latex.LatexCommand;
import org.texttechnologylab.parliament_browser_3_4.latex.LatexEnvironment;

import java.util.List;

/**
 * Creates template and Latex output for instances of protocol objects.
 * @author Tim König
 */
public class ProtocolLatexTemplate implements LatexTemplate {

    private Protocol protocolSource;
    private StringBuilder outputLatex;
    private static StringBuilder outputTemplate;

    /**
     * Default constructor
     * @author Tim König
     */
    public ProtocolLatexTemplate() {
        this.outputLatex = new StringBuilder();
    }

    /**
     * Constructor with Protocol object as parameter
     * @param protocol Protocol object
     * @author Tim König
     */
    public ProtocolLatexTemplate(Protocol protocol) {
        this.outputLatex = new StringBuilder();
        this.protocolSource = protocol;
        this.createLatexOutput();
    }

    /**
     * Initialize Latex template
     * @author Tim König
     */
    private static void initTemplate() {
        outputTemplate = new StringBuilder();
        LatexEnvironment largeEnv = new LatexEnvironment("large");
        LatexEnvironment centerEnv = new LatexEnvironment("center");
        centerEnv.addText("Protokoll des deutschen Bundestages\\\\");
        centerEnv.addText("Sitzungsnummer: <protocolNumber>\\\\");
        centerEnv.addText("Legislaturperiode: <protocolPeriod>\\\\");
        centerEnv.addText("Datum: <protocolDate>\n");
        largeEnv.addText(centerEnv.toString());
        outputTemplate.append(largeEnv);
        LatexCommand newPageCmd = new LatexCommand("newpage");
        outputTemplate.append(newPageCmd);
        LatexCommand setCounterCmd = new LatexCommand("setcounter", "tocdepth", "2");
        outputTemplate.append(setCounterCmd);
        LatexCommand tableContentCmd = new LatexCommand("tableofcontents");
        outputTemplate.append(tableContentCmd);
        outputTemplate.append(newPageCmd).append("\n");
        outputTemplate.append("<dayTopicsContent>");
    }

    /**
     * Creates latex code by replacing the template accordingly
     * @author Tim König
     */
    @Override
    public void createLatexOutput() {
        if (outputTemplate == null) initTemplate();
        this.outputLatex.setLength(0);
        StringBuilder dayTopicsText = new StringBuilder();
        //create Latex code for every speech in daytopic as big string
        List<DayTopic> dayTopicSet = this.protocolSource.getDayTopicList();
        dayTopicSet.forEach(elem -> {
            DayTopicLatexTemplate dayTopicTemplate = new DayTopicLatexTemplate(elem);
            dayTopicsText.append(dayTopicTemplate.getLatexOutput());
        });
        // create final latex output by replacing template variables with latex code
        this.outputLatex.append(outputTemplate.toString()
                .replace("<protocolNumber>", Integer.toString(this.protocolSource.getNum()))
                .replace("<protocolPeriod>", Integer.toString(this.protocolSource.getLegislaturePeriod()))
                .replace("<protocolDate>", this.protocolSource.getDateFormatted())
                .replace("<dayTopicsContent>", dayTopicsText));
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
