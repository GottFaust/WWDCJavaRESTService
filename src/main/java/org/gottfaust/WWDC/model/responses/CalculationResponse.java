package org.gottfaust.WWDC.model.responses;

import org.gottfaust.WWDC.model.builders.CalculationResponseBuilder;
import org.gottfaust.WWDC.model.interfaces.IWWDCResponse;

import java.util.HashMap;

public class CalculationResponse implements IWWDCResponse {

    /** The IHateos self link **/
    private String self;

    /** The calculation results **/
    public String results;

    /**
     * CTOR
     * @param builder CalculationResponseBuilder to build with
     */
    public CalculationResponse(CalculationResponseBuilder builder){
        this.self = builder.self;
        this.results = builder.results;
    }

    @Override
    public HashMap get_links() {
        HashMap map = new HashMap();
        map.put("Self", this.self);
        return map;
    }
}
