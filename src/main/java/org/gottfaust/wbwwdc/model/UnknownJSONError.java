package org.gottfaust.wbwwdc.model;

import org.gottfaust.wbwwdc.model.abstracts.AJSONError;
import org.gottfaust.wbwwdc.model.builders.UnknownJSONErrorBuilder;

public class UnknownJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder UnknownJSONErrorBuilder to build this error using
     */
    public UnknownJSONError(UnknownJSONErrorBuilder builder) {
        super(builder);
    }
}
