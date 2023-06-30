package org.texttechnologylab.parliament_browser_3_4.latex;

import org.texttechnologylab.parliament_browser_3_4.latex.template.LatexTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/**
 * Takes care of saving .tex File and compiling pdf from a LatexTemplate Object.
 * @author Tim König
 */
public class LatexBuilder {

    private String resourcePath;
    private String resourceFilePath;
    private StringBuilder outputDoc;
    private Boolean debug = false;
    private Boolean warning = false;
    private String renderer;

    /**
     * Default-constructor for LatexBuilder-class
     * @author Tim König
     */
    public LatexBuilder() {
        this.outputDoc = new StringBuilder();
        this.resourceFilePath = "";
        this.resourcePath = "";
        this.renderer = "";
    }

    /**
     * Initializes Latex document by defining documentclass, including packages, etc.
     * @param template LatexTemplate object
     * @author Tim König
     */
    private void createHeadofDoc(LatexTemplate template) {
        this.outputDoc = new StringBuilder();
        LatexCommand docClassCmd = new LatexCommand("documentclass", "article");
        docClassCmd.addOptions("a4paper", "12pt");
        LatexCommand inputencPkg = new LatexCommand("usepackage", "inputenc");
        inputencPkg.addOptions("utf8");
        LatexCommand fontencPkg = new LatexCommand("usepackage", "fontenc");
        //fontencPkg.addOptions("LGR");
        fontencPkg.addOptions("T1");
        LatexCommand floatPkg = new LatexCommand("usepackage", "float");
        LatexCommand graphicxPkg = new LatexCommand("usepackage", "graphicx");
        LatexCommand hyperrefPkg = new LatexCommand("usepackage", "hyperref");
        LatexCommand charCmd202F = new LatexCommand("DeclareUnicodeCharacter", "202F", "\\,");
        LatexCommand charCmd02BC = new LatexCommand("DeclareUnicodeCharacter", "02BC", "'");
        LatexCommand charCmd2003 = new LatexCommand("DeclareUnicodeCharacter", "2003", "\\,");
        LatexCommand charCmd2153 = new LatexCommand("DeclareUnicodeCharacter", "2153", ",33");
        LatexCommand charCmd2005 = new LatexCommand("DeclareUnicodeCharacter", "2005", "\\,");
        LatexCommand charCmd2009 = new LatexCommand("DeclareUnicodeCharacter", "2009", "\\,");
        LatexCommand charCmd03CA = new LatexCommand("DeclareUnicodeCharacter", "03CA", "i");
        LatexCommand charCmd05C5 = new LatexCommand("DeclareUnicodeCharacter", "05C5", ".");
        LatexCommand charCmd2002 = new LatexCommand("DeclareUnicodeCharacter", "2002", "");
        LatexCommand charCmd2027 = new LatexCommand("DeclareUnicodeCharacter", "2027", "-");
        LatexCommand charCmd1FBD = new LatexCommand("DeclareUnicodeCharacter", "1FBD", "'");
        LatexCommand charCmd0207 = new LatexCommand("DeclareUnicodeCharacter", "0207", "e");
        LatexCommand charCmd0196 = new LatexCommand("DeclareUnicodeCharacter", "0196", "I");
        LatexCommand charCmd2212 = new LatexCommand("DeclareUnicodeCharacter", "2212", "-");
        this.outputDoc.append(docClassCmd);
        this.outputDoc.append(inputencPkg);
        this.outputDoc.append(fontencPkg);
        this.outputDoc.append(charCmd202F);
        this.outputDoc.append(charCmd02BC);
        this.outputDoc.append(charCmd2003);
        this.outputDoc.append(charCmd2153);
        this.outputDoc.append(charCmd2212);
        this.outputDoc.append(charCmd2005);
        this.outputDoc.append(charCmd2009);
        this.outputDoc.append(charCmd03CA);
        this.outputDoc.append(charCmd05C5);
        this.outputDoc.append(charCmd2002);
        this.outputDoc.append(charCmd2027);
        this.outputDoc.append(charCmd0196);
        this.outputDoc.append(charCmd0207);
        this.outputDoc.append(charCmd1FBD);
        this.outputDoc.append(floatPkg);
        this.outputDoc.append(graphicxPkg);
        this.outputDoc.append(hyperrefPkg);
        this.outputDoc.append("\n");
        LatexEnvironment documentEnv = new LatexEnvironment("document");
        documentEnv.addText(template.getLatexOutput());
        this.outputDoc.append(documentEnv);
    }

    /**
     * Set debug flag for console output
     * @param flag bool value to set
     * @author Tim König
     */
    public void debug(Boolean flag) {
        this.debug = flag;
    }

    /**
     * Set Warning flag for console output
     * @param flag bool value to set
     * @author Tim König
     */
    public void warning(Boolean flag) {
        this.debug = flag;
    }

    /**
     * Saves created string from template object to Latex-file
     * @param latexTemplate Template of latex file
     * @param filePath      path to save Latex and PDF-File
     * @param fileName      name of Latex-File
     * @param renderer      name of Latex binary to render PDF-File
     * @author Tim König
     */
    public void saveTexFile(LatexTemplate latexTemplate, String filePath, String fileName, String renderer) {
        this.renderer = renderer;
        this.resourcePath = System.getProperty("user.dir") + File.separator + filePath;
        this.resourceFilePath = this.resourcePath + File.separator + fileName;
        this.createHeadofDoc(latexTemplate);
        try {
            new File(this.resourcePath).mkdir();
            FileWriter writer = new FileWriter(this.resourceFilePath + ".tex");
            writer.write(this.outputDoc.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * create PDF by first saving .tex-File,
     * then render PDF-File twice (to get table of content and references right)
     * @param latexTemplate LatexTemplate object
     * @author Tim König
     */
    public void createPDF(LatexTemplate latexTemplate, String FilePath, String FileName, String renderer) {
        this.saveTexFile(latexTemplate, FilePath, FileName, renderer);
        this.renderPDF();
        this.renderPDF();
    }

    /**
     * Compiling pdf by defined renderer (pdflatex) and resource paths
     * @author Tim König
     */
    public void renderPDF() {
        Runtime runtime = Runtime.getRuntime();
        String command = this.renderer + " " + this.resourceFilePath + ".tex";
        try {
            Process pdfLatex = runtime.exec(command, null, new File(this.resourcePath));
            if (this.debug) {
                Scanner sc = new Scanner(pdfLatex.getInputStream());
                while (sc.hasNextLine()) {
                    System.out.println(sc.nextLine());
                }
            } else if (this.warning) {
                Scanner sc = new Scanner(pdfLatex.getInputStream());
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if (line.toLowerCase().contains("warning"))
                        System.out.println(sc.nextLine());
                }
            } else {
                Scanner sc = new Scanner(pdfLatex.getInputStream());
                while (sc.hasNextLine()) {
                    sc.nextLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!debug) {
            File auxFile = new File(this.resourceFilePath + ".aux");
            auxFile.deleteOnExit();
            File logFile = new File(this.resourceFilePath + ".log");
            logFile.deleteOnExit();
            File texFile = new File(this.resourceFilePath + ".tex");
            texFile.deleteOnExit();
            File tocFile = new File(this.resourceFilePath + ".toc");
            if (tocFile.exists())
                tocFile.deleteOnExit();
            File outFile = new File(this.resourceFilePath + ".out");
            if (outFile.exists())
                outFile.deleteOnExit();
        }

    }
}
