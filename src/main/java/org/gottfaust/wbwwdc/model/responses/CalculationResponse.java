package org.gottfaust.wbwwdc.model.responses;

import org.gottfaust.wbwwdc.model.builders.CalculationResponseBuilder;
import org.gottfaust.wbwwdc.model.interfaces.IWBWWDCResponse;

import java.util.HashMap;

public class CalculationResponse implements IWBWWDCResponse {

    /** The IHateos self link **/
    private String self;

    /**
     * CTOR
     * @param builder CalculationResponseBuilder to build with
     */
    public CalculationResponse(CalculationResponseBuilder builder){
        this.self = builder.self;
    }

    @Override
    public HashMap get_links() {
        HashMap map = new HashMap();
        map.put("Self", this.self);
        return map;
    }
}
