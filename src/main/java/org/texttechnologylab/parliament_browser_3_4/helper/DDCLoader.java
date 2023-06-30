package org.texttechnologylab.parliament_browser_3_4.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class to load the DDC categories from our ddc csv file.
 * @author Simon Schütt
 */
public class DDCLoader {

    /** DDC category storage, with the key being the category number and the value being the loaded name */
    private HashMap<Integer, String> ddcs;

    /**
     * Constructor of DDCLoader
     * @author Simon Schütt
     */
    public DDCLoader() {
        ddcs = new HashMap<>();
    }

    /**
     * Loads the DDC categories out of the given csv file into the ddcs hashmap.
     * @param file Reference of ddc csv file.
     * @author Simon Schütt
     */
    public void loadDDCfromCSV(File file) {
        Scanner scan = null;
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error: CSV file not found.");
            return;
        }

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (!line.isEmpty()) {
                int ddcNumber = Integer.parseInt(line.split("\t", 2)[0]);
                String ddcName = line.split("\t", 2)[1];
                ddcs.put(ddcNumber, ddcName);
            }
        }
    }

    /**
     * Returns the ddc names map.
     * @return ddc names map
     * @author Simon Schütt
     */
    public HashMap<Integer, String> getDDCs() {
        return this.ddcs;
    }

}
