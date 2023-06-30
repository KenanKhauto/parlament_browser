package org.texttechnologylab.parliament_browser_3_4.latex.template;

import org.texttechnologylab.parliament_browser_3_4.data.Comment;
import org.texttechnologylab.parliament_browser_3_4.data.Speech;
import org.texttechnologylab.parliament_browser_3_4.latex.LatexCommand;

import java.util.List;

/**
 * Creates template and Latex output for instances of speech objects.
 * @author Tim König
 */
public class SpeechLatexTemplate implements LatexTemplate {

    private static StringBuilder outputTemplate;
    private StringBuilder outputLatex;
    private Speech speechSource;

    /**
     * Default-Constructor
     * @author Tim König
     */
    public SpeechLatexTemplate() {
        this.outputLatex = new StringBuilder();
    }

    /**
     * Constructor with Speech object argument
     * @param speechObj Speech object
     * @author Tim König
     */
    public SpeechLatexTemplate(Speech speechObj) {
        this.outputLatex = new StringBuilder();
        this.speechSource = speechObj;
        this.createLatexOutput();
    }

    /**
     * Initialize Latex template
     * @author Tim König
     */
    private static void initTemplate() {
        outputTemplate = new StringBuilder();
        LatexCommand titleSection = new LatexCommand("subsection", "Rede von <speakerName> (<partyName>)");
        outputTemplate.append(titleSection);
        LatexCommand contentSubSection = new LatexCommand("subsubsection", "Inhalt");
        outputTemplate.append(contentSubSection);
        outputTemplate.append("<speechContent>\n");
        LatexCommand commentarySubSection = new LatexCommand("subsubsection", "Kommentare");
        outputTemplate.append(commentarySubSection);
        outputTemplate.append("<commentaryContent>");
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
     * Creates latex code by replacing the template accordingly
     * @author Tim König
     */
    @Override
    public void createLatexOutput() {
        if (outputTemplate == null) initTemplate();
        List<Comment> commentList = this.speechSource.getComments();
        StringBuilder commentText = new StringBuilder();
        commentList.forEach(elem -> commentText.append(elem.getContent()
                .replace("#", "\\#")
                .replace("&", "\\&").trim() + "\\\\[15pt]"));
        this.outputLatex.setLength(0);
        if (this.speechSource.getSpeaker() != null) {
            this.outputLatex.append(outputTemplate.toString()
                    .replace("<speakerName>", this.speechSource.getSpeaker().getFirstName() + " " +
                            this.speechSource.getSpeaker().getLastName())
                    .replace("<partyName>", this.speechSource.getSpeaker().getParty().getName())
                    .replace("<speechContent>", this.speechSource.getText()
                            .replace("#", "\\#")
                            .replace("&", "\\&")
                            .replace("_", "\\_")
                            .replace("Χρόνια Πολλά Ελλάδα! Ζήτω το ελληνικό έθνος", "Chronia Polla Ellada! Zito to elliniko ethnos"))
                    .replace("<commentaryContent>", commentText.toString()));
        } else {
            this.outputLatex.append(outputTemplate.toString()
                    .replace("<speakerName>", "N.A.")
                    .replace("<partyName>", "N.A.")
                    .replace("<speechContent>", this.speechSource.getText()
                            .replace("#", "\\#")
                            .replace("&", "\\&")
                            .replace("_", "\\_")
                            .replace("Χρόνια Πολλά Ελλάδα! Ζήτω το ελληνικό έθνος", "Chronia Polla Ellada! Zito to elliniko ethnos"))
                    .replace("<commentaryContent>", commentText.toString()));
        }
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
