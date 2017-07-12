package org.gottfaust.wbwwdc.model.factories;

import org.gottfaust.wbwwdc.model.DataJSONError;
import org.gottfaust.wbwwdc.model.DatasourceJSONError;
import org.gottfaust.wbwwdc.model.ProcessingJSONError;
import org.gottfaust.wbwwdc.model.UnknownJSONError;
import org.gottfaust.wbwwdc.model.abstracts.AJSONError;
import org.gottfaust.wbwwdc.model.builders.DataJSONErrorBuilder;
import org.gottfaust.wbwwdc.model.builders.DatasourceJSONErrorBuilder;
import org.gottfaust.wbwwdc.model.builders.ProcessingJSONErrorBuilder;
import org.gottfaust.wbwwdc.model.builders.UnknownJSONErrorBuilder;
import org.gottfaust.wbwwdc.model.enums.JSONErrorType;
import org.gottfaust.wbwwdc.model.interfaces.IJSONErrorBuilder;

public class JSONErrorFactory {

    /**
     * Builds an AJSONError based on the supplied JSONErrorType using the builder provided
     * @param builder IJSONErrorBuilder to use in building the AJSONError
     * @param type Type of AJSONError to build
     * @return built AJSONError
     */
    public static AJSONError buildJSONError(IJSONErrorBuilder builder, JSONErrorType type){
        AJSONError error = null;
        switch(type){
            case DATASOURCE:
                error = new DatasourceJSONError((DatasourceJSONErrorBuilder) builder);
                break;
            case DATA:
                error = new DataJSONError((DataJSONErrorBuilder) builder);
                break;
            case PROCESSING:
                error = new ProcessingJSONError((ProcessingJSONErrorBuilder) builder);
                break;
            case UNKNOWN:
                error = new UnknownJSONError((UnknownJSONErrorBuilder) builder);
                break;
        }
        return error;
    }
}
