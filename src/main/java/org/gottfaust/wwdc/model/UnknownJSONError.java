package org.gottfaust.wwdc.model;

import org.gottfaust.wwdc.model.abstracts.AJSONError;
import org.gottfaust.wwdc.model.builders.UnknownJSONErrorBuilder;

public class UnknownJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder UnknownJSONErrorBuilder to build this error using
     */
    public UnknownJSONError(UnknownJSONErrorBuilder builder) {
        super(builder);
    }
}
