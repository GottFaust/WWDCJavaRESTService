package org.gottfaust.wwdc.model.interfaces;

import org.gottfaust.wwdc.model.enums.JSONErrorType;

import java.util.HashMap;

public interface IJSONErrorBuilder {

    /**
     * Gets the error description
     * @return String: error description
     */
    String getDescription();

    /**
     * Gets the error type
     * @return JSONErrorType: error type
     */
    JSONErrorType getType();

    /**
     * Gets the HATEOS links
     * @return HashMap: HATEOS links
     */
    HashMap getLinks();

    /**
     *  Gets the error title
     * @return String: error title
     */
    String getTitle();
}
