package org.texttechnologylab.parliament_browser_3_4.data;

public interface PlenaryObject extends Comparable<PlenaryObject> {


    /**
     * get the Factory of the Object
     * @return Factory
     */
    Factory getFactory();

    /**
     * get the ID of the Object as a String
     * @return id as a String
     */
    String getID();

    /**
     * Set the ID as a String
     * @param value id as a String
     */
    void setID(String value);


}
