package org.gottfaust.wbwwdc.model;

import org.gottfaust.wbwwdc.model.abstracts.AJSONError;
import org.gottfaust.wbwwdc.model.builders.DatasourceJSONErrorBuilder;

public class DatasourceJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder DatasourceJSONErrorBuilder to build this error using
     */
    public DatasourceJSONError(DatasourceJSONErrorBuilder builder) {
        super(builder);
    }
}
