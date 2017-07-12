package org.gottfaust.WWDC.model.factories;

import org.gottfaust.WWDC.model.builders.*;
import org.gottfaust.WWDC.model.interfaces.IWWDCResponseBuilder;
import org.gottfaust.WWDC.model.responses.*;
import org.gottfaust.WWDC.model.interfaces.IWWDCResponse;

public class ResponseFactory {

    /**
     * Factory method to build proper responses from the supplied builder
     * @param builder IWWDCResponseBuilder to use to build the response
     * @return Built response or null on failure
     */
    public static IWWDCResponse buildResponse(IWWDCResponseBuilder builder){

        //Get the builder's class
        String builderClass = builder.getClass().getSimpleName();

        //Setup the response
        IWWDCResponse response = null;

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
