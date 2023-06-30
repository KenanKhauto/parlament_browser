package org.texttechnologylab.parliament_browser_3_4.data;

import java.util.List;

public interface Parliament {

    /**
     * get factory
     * @return Factory
     */
    Factory getFactory();

    /**
     * Get Protocols
     * @return List of Protocols
     */
    List<Protocol> getProtocols();

    /**
     * Add a protocol to the Protocol list.
     * @param value Protocol
     */
    void addProtocol(Protocol value);

    /**
     * Add many protocols to the protocol list
     * @param value list of protocols
     */
    void addProtocols(List<Protocol> value);

}
