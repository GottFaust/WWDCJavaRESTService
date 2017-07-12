package org.gottfaust.wwdc.model;

import org.gottfaust.wwdc.model.abstracts.AJSONError;
import org.gottfaust.wwdc.model.builders.DataJSONErrorBuilder;

public class DataJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder DataJSONErrorBuilder to build this error using
     */
    public DataJSONError(DataJSONErrorBuilder builder) {
        super(builder);
    }
}
