package org.gottfaust.wwdc.model;

import org.gottfaust.wwdc.model.abstracts.AJSONError;
import org.gottfaust.wwdc.model.builders.ProcessingJSONErrorBuilder;

public class ProcessingJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder ProcessingJSONErrorBuilder to build this error using
     */
    public ProcessingJSONError(ProcessingJSONErrorBuilder builder) {
        super(builder);
    }
}
