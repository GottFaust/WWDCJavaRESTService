package org.gottfaust.WWDC.model;

import org.gottfaust.WWDC.model.abstracts.AJSONError;
import org.gottfaust.WWDC.model.builders.DataJSONErrorBuilder;

public class DataJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder DataJSONErrorBuilder to build this error using
     */
    public DataJSONError(DataJSONErrorBuilder builder) {
        super(builder);
    }
}
