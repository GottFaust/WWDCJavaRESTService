package org.gottfaust.wwdc.model.factories;

import org.gottfaust.wwdc.model.DataJSONError;
import org.gottfaust.wwdc.model.DatasourceJSONError;
import org.gottfaust.wwdc.model.ProcessingJSONError;
import org.gottfaust.wwdc.model.UnknownJSONError;
import org.gottfaust.wwdc.model.abstracts.AJSONError;
import org.gottfaust.wwdc.model.builders.DataJSONErrorBuilder;
import org.gottfaust.wwdc.model.builders.DatasourceJSONErrorBuilder;
import org.gottfaust.wwdc.model.builders.ProcessingJSONErrorBuilder;
import org.gottfaust.wwdc.model.builders.UnknownJSONErrorBuilder;
import org.gottfaust.wwdc.model.enums.JSONErrorType;
import org.gottfaust.wwdc.model.interfaces.IJSONErrorBuilder;

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
