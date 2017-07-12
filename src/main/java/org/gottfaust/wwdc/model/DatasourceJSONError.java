package org.gottfaust.wwdc.model;

import org.gottfaust.wwdc.model.abstracts.AJSONError;
import org.gottfaust.wwdc.model.builders.DatasourceJSONErrorBuilder;

public class DatasourceJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder DatasourceJSONErrorBuilder to build this error using
     */
    public DatasourceJSONError(DatasourceJSONErrorBuilder builder) {
        super(builder);
    }
}
