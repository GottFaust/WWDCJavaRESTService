package org.gottfaust.wwdc.model.responses;

import org.gottfaust.wwdc.model.builders.CalculationResponseBuilder;
import org.gottfaust.wwdc.model.interfaces.IwwdcResponse;

import java.util.HashMap;

public class CalculationResponse implements IwwdcResponse {

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
