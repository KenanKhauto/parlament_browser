package org.texttechnologylab.parliament_browser_3_4.latex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for constructing basic Latex environments.
 * Template: \begin{environmentName}[options]
 * body of environment
 * \end{environmentName}
 * @author Tim König
 */
public class LatexEnvironment {

    private String name;
    private List<String> optionsList;
    private StringBuilder content = new StringBuilder();
    private Boolean changed = false;
    private StringBuilder output = new StringBuilder();

    /**
     * constructor for LatexEnvironment-Class
     * @param envName name of Latex environment
     * @author Tim König
     */
    public LatexEnvironment(String envName) {
        this.optionsList = new ArrayList<>(0);
        this.name = envName;
    }

    /**
     * alternative Constructor to directly add options
     * @param envName name of Latex environment
     * @param options list of options for Latex environment
     * @author Tim König
     */
    public LatexEnvironment(String envName, List<String> options) {
        this.name = envName;
        this.optionsList = options;
    }

    /**
     * add Options to Latex environment
     * @param opts options for Latex environment (as Strings)
     * @author Tim König
     */
    public void addOptions(String... opts) {
        this.optionsList.addAll(Arrays.asList(opts));
        this.changed = true;
    }

    /**
     * Add content to body of latex environment
     * @param text content as String
     * @author Tim König
     */
    public void addText(String text) {
        this.content.append(text).append("\n");
        this.changed = true;
    }

    /**
     * Construct the output-string for a Latex environment.
     * @return output as string
     * @author Tim König
     */
    public String toString() {
        if (this.output.length() == 0 || this.changed) {
            this.output.setLength(0);
            // Add start of environment ("\begin{envName}")
            this.output.append("\\begin{" + this.name + "}");
            // Add options
            if (!this.optionsList.isEmpty())
                this.output.append("[" + String.join(",", this.optionsList) + "]");
            this.output.append("\n\n");
            // Add body of environment
            this.output.append(this.content);
            //this.output.append("\n");
            // Add End of environment ("\end{envName}")
            this.output.append("\\end{" + this.name + "}");
            this.output.append("\n");
            this.changed = false;
        }
        return this.output.toString();
    }
}
