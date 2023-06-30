package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.texttechnologylab.parliament_browser_3_4.data.Factory;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

/**
 * A class for parsing speakers that are mentioned in random nodes
 * @author kenan Khauto
 */

public class Speaker_Plain_File_Impl extends Speaker_File_Impl {

    public static int counter = 1;

    /**
     * Constructor
     * @param pFactory
     */
    public Speaker_Plain_File_Impl(Factory pFactory, Node pNode) {
        super(pFactory);
        this.setID("ID-" + counter);
        counter += 1;

        this.setName(pNode.getTextContent());

        if (this.getName().startsWith("Präsident")) {
            this.setRole("Präsident");
        } else if (this.getName().startsWith("Vizepräsidentin")) {
            this.setRole("Vizepräsidentin");
        } else if (this.getName().startsWith("Vizepräsident")) {
            this.setRole("Vizepräsident");
        }

        this.setName(this.getName().replaceAll(this.getRole(), ""));

        if (this.getName().startsWith(" ")) {
            this.setName(this.getName().replaceFirst(" ", ""));
        }
        if (this.getName().contains("Dr.")) {
            this.setTitle("Dr.");
            this.setName(this.getName().replaceAll("Dr.", ""));
        }
        List<String> firstLast = Arrays.asList(this.getName().split(" "));
        if (firstLast.size() == 2) {

            this.setFirstName(firstLast.get(0));
            this.setLastName(firstLast.get(1));
        }


    }

    @Override
    public void setName(String sValue) {
        super.setName(transform(sValue));
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean isLeader() {
        return super.isLeader();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Transform-Method for some "errors" in the data
     * @param sValue
     * @return
     */
    public static String transform(String sValue) {

        String sReturn = sValue;

        sReturn = sReturn.replaceAll("Vizepräsident in", "Vizepräsidentin");
        sReturn = sReturn.replaceAll("Vizepräsiden t", "Vizepräsident");
        sReturn = sReturn.replaceAll(":", "");

        return sReturn;

    }
}

