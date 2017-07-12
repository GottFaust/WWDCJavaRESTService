package org.gottfaust.wbwwdc.model.builders;

import org.gottfaust.wbwwdc.model.abstracts.AJSONErrorBuilder;
import org.gottfaust.wbwwdc.model.enums.JSONErrorType;

import java.util.HashMap;

public class ProcessingJSONErrorBuilder extends AJSONErrorBuilder {

    /**
     * CTOR
     * @param description The error's description
     * @param title The error's title
     * @param links The error's links
     */
    public ProcessingJSONErrorBuilder(String description, String title, HashMap links) {
        super(description, title, JSONErrorType.PROCESSING, links);
    }
}
