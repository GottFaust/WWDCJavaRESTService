package org.gottfaust.wbwwdc.model.factories;

import org.gottfaust.wbwwdc.model.builders.*;
import org.gottfaust.wbwwdc.model.interfaces.IWBWWDCResponseBuilder;
import org.gottfaust.wbwwdc.model.responses.*;
import org.gottfaust.wbwwdc.model.interfaces.IWBWWDCResponse;

public class ResponseFactory {

    /**
     * Factory method to build proper responses from the supplied builder
     * @param builder IWBWWDCResponseBuilder to use to build the response
     * @return Built response or null on failure
     */
    public static IWBWWDCResponse buildResponse(IWBWWDCResponseBuilder builder){

        //Get the builder's class
        String builderClass = builder.getClass().getSimpleName();

        //Setup the response
        IWBWWDCResponse response = null;

        //Switch on builder type and construct the appropriate response
        switch(builderClass){
            case "CalculationResponseBuilder":
                response = new CalculationResponse((CalculationResponseBuilder)builder);
                break;
        }

        //Return the constructed response
        return response;
    }
}
