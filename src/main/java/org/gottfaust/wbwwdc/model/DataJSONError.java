package org.gottfaust.wbwwdc.model;

import org.gottfaust.wbwwdc.model.abstracts.AJSONError;
import org.gottfaust.wbwwdc.model.builders.DataJSONErrorBuilder;

public class DataJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder DataJSONErrorBuilder to build this error using
     */
    public DataJSONError(DataJSONErrorBuilder builder) {
        super(builder);
    }
}
