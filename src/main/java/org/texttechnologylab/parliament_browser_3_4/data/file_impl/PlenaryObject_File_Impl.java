package org.texttechnologylab.parliament_browser_3_4.data.file_impl;

import org.texttechnologylab.parliament_browser_3_4.data.Factory;
import org.texttechnologylab.parliament_browser_3_4.data.PlenaryObject;


/**
 * @author kenan Khauto
 */

public class PlenaryObject_File_Impl implements PlenaryObject {

    private String ID = "";

    protected Factory factory = null;


    /**
     * Constructor
     * @param pFactory Factory
     */
    public PlenaryObject_File_Impl(Factory pFactory) {
        this.factory = pFactory;
    }

    public PlenaryObject_File_Impl() {

    }

    @Override
    public Factory getFactory() {
        return this.factory;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public void setID(String value) {
        this.ID = value;
    }


    @Override
    public int compareTo(PlenaryObject o) {
        return getID().compareTo(o.getID());
    }

    @Override
    public boolean equals(Object o) {
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return getID().hashCode();
    }
}
