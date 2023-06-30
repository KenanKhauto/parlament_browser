package org.texttechnologylab.parliament_browser_3_4.latex.template;

/**
 * Interface for a latex template to produce latex code and template for different classes from data-package
 */
public interface LatexTemplate {

    /**
     * Getter-method for Latex code as String
     * @return Latex code as String
     * @author Tim König
     */
    String getLatexOutput();

    /**
     * Creates latex code by replacing the template accordingly
     * @author Tim König
     */
    void createLatexOutput();

    /**
     * Getter for Speech template
     * @return Template as String
     * @author Tim König
     */
    static String getTemplate() {
        return null;
    }

    /**
     * Updates content of template
     * @param string Content to update template (as String)
     * @author Tim König
     */
    static void updateTemplate(String string) {
    }

}
