package org.gottfaust.WWDC.model;

import org.gottfaust.WWDC.model.abstracts.AJSONError;
import org.gottfaust.WWDC.model.builders.ProcessingJSONErrorBuilder;

public class ProcessingJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder ProcessingJSONErrorBuilder to build this error using
     */
    public ProcessingJSONError(ProcessingJSONErrorBuilder builder) {
        super(builder);
    }
}
