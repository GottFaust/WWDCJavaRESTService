package org.gottfaust.WWDC.model;

import org.gottfaust.WWDC.model.abstracts.AJSONError;
import org.gottfaust.WWDC.model.builders.UnknownJSONErrorBuilder;

public class UnknownJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder UnknownJSONErrorBuilder to build this error using
     */
    public UnknownJSONError(UnknownJSONErrorBuilder builder) {
        super(builder);
    }
}
