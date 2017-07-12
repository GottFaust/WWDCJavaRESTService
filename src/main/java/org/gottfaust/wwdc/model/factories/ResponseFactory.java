package org.gottfaust.wwdc.model.factories;

import org.gottfaust.wwdc.model.builders.*;
import org.gottfaust.wwdc.model.interfaces.IwwdcResponseBuilder;
import org.gottfaust.wwdc.model.responses.*;
import org.gottfaust.wwdc.model.interfaces.IwwdcResponse;

public class ResponseFactory {

    /**
     * Factory method to build proper responses from the supplied builder
     * @param builder IwwdcResponseBuilder to use to build the response
     * @return Built response or null on failure
     */
    public static IwwdcResponse buildResponse(IwwdcResponseBuilder builder){

        //Get the builder's class
        String builderClass = builder.getClass().getSimpleName();

        //Setup the response
        IwwdcResponse response = null;

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
