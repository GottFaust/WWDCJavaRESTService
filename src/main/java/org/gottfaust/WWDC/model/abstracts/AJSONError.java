package org.gottfaust.WWDC.model.abstracts;

import org.gottfaust.WWDC.model.enums.JSONErrorType;
import org.gottfaust.WWDC.model.interfaces.IJSONError;
import org.gottfaust.WWDC.model.interfaces.IJSONErrorBuilder;

import java.util.HashMap;

public abstract class AJSONError implements IJSONError {

    /** Description of this error **/
    private String description;

    /** This error's title **/
    private String title;

    /** This error's type **/
    private JSONErrorType type;

    /** Any HATEOS links, if applicable **/
    private HashMap links;

    /**
     * CTOR
     * @param builder IJSONErrorBuilder to build the AJSONError
     */
    public AJSONError(IJSONErrorBuilder builder){

        this.description = builder.getDescription();
        this.title = builder.getTitle();
        this.type = builder.getType();
        this.links = builder.getLinks();
    }

    /** Get Methods **/
    public String getDescription() { return description; }
    public String getTitle() { return title; }
    public JSONErrorType getType() { return type; }

    @Override
    public HashMap get_links() {
        if(links == null) {
            HashMap links = new HashMap();
            links.put("self", "");
        }
        return links;
    }
}
