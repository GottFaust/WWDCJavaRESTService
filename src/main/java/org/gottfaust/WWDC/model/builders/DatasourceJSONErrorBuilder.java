package org.gottfaust.WWDC.model.builders;

import org.gottfaust.WWDC.model.abstracts.AJSONErrorBuilder;
import org.gottfaust.WWDC.model.enums.JSONErrorType;

import java.util.HashMap;

public class DatasourceJSONErrorBuilder extends AJSONErrorBuilder {

    /**
     * CTOR
     * @param description The error's description
     * @param title The error's title
     * @param links The error's links
     */
    public DatasourceJSONErrorBuilder(String description, String title, HashMap links) {
        super(description, title, JSONErrorType.DATASOURCE, links);
    }
}
