package org.gottfaust.wbwwdc.model;

import org.gottfaust.wbwwdc.model.abstracts.AJSONError;
import org.gottfaust.wbwwdc.model.builders.ProcessingJSONErrorBuilder;

public class ProcessingJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder ProcessingJSONErrorBuilder to build this error using
     */
    public ProcessingJSONError(ProcessingJSONErrorBuilder builder) {
        super(builder);
    }
}
