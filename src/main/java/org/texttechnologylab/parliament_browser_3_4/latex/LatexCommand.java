package org.texttechnologylab.parliament_browser_3_4.latex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for constructing basic Latex commands.
 * Template: /commandname[options]{arguments}
 * @author Tim König
 */
public class LatexCommand {

    private String name;
    private List<String> optionsList;
    private List<String> argumentsList;
    private StringBuilder output;

    /**
     * * Constructor for Latex command with arguments
     * example: \tableofcontents
     * @param cmdName name of latex command
     * @param args    arguments for latex command
     * @author Tim König
     */
    public LatexCommand(String cmdName, String... args) {
        this.argumentsList = new ArrayList<>(0);
        this.name = cmdName;
        this.argumentsList.addAll(Arrays.asList(args));
    }

    /**
     * * Constructor for Latex command without arguments and options
     * example:
     * @param cmdName name of latex command
     * @author Tim König
     */
    public LatexCommand(String cmdName) {
        this.name = cmdName;
    }

    /**
     * Alternative constructor with arguments and options
     * @param cmdName name of latex command
     * @param options list of options for latex command
     * @param args    arguments for latex command
     * @author Tim König
     */
    public LatexCommand(String cmdName, List<String> options, String... args) {
        this.argumentsList = new ArrayList<>(0);
        this.name = cmdName;
        this.optionsList = options;
        this.argumentsList.addAll(Arrays.asList(args));
    }

    /**
     * add options to Latex command
     * @param opts options for Latex command (as String)
     * @author Tim König
     */
    public void addOptions(String... opts) {
        if (this.optionsList == null)
            this.optionsList = new ArrayList<>(0);
        this.optionsList.addAll(Arrays.asList(opts));
    }

    /**
     * add arguments to Latex command
     * @param args arguments for Latex command (as String)
     * @author Tim König
     */
    public void addArguments(String... args) {
        if (this.argumentsList == null)
            this.argumentsList = new ArrayList<>(0);
        this.argumentsList.addAll(Arrays.asList(args));
    }

    /**
     * Construct the output-string for a Latex command.
     * @return output as string
     * @author Tim König
     */
    public String toString() {
        if (this.output == null) {
            this.output = new StringBuilder();
            // Create start of command with "\cmdName"
            this.output.append("\\" + this.name);
            // Add options
            if (this.optionsList != null)
                if (!this.optionsList.isEmpty())
                    this.output.append("[" + String.join(",", this.optionsList) + "]");
            // Add arguments
            if (this.argumentsList != null)
                if (!this.argumentsList.isEmpty())
                    this.argumentsList.forEach(elem -> this.output.append("{" + elem + "}"));
            this.output.append("\n");
        }
        return this.output.toString();
    }
}
