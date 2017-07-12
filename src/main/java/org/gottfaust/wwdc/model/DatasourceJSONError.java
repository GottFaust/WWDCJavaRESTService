package org.gottfaust.WWDC.model;

import org.gottfaust.WWDC.model.abstracts.AJSONError;
import org.gottfaust.WWDC.model.builders.DatasourceJSONErrorBuilder;

public class DatasourceJSONError extends AJSONError {

    /**
     * CTOR
     * @param builder DatasourceJSONErrorBuilder to build this error using
     */
    public DatasourceJSONError(DatasourceJSONErrorBuilder builder) {
        super(builder);
    }
}
