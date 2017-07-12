package org.gottfaust.wwdc.model.abstracts;

import org.gottfaust.wwdc.model.enums.JSONErrorType;
import org.gottfaust.wwdc.model.interfaces.IJSONErrorBuilder;

import java.util.HashMap;

public class AJSONErrorBuilder implements IJSONErrorBuilder {

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
     * @param description The error's description
     * @param title The error's title
     * @param type The error's type
     * @param links The error's links
     */
    public AJSONErrorBuilder(String description,
                             String title,
                             JSONErrorType type,
                             HashMap links)
    {
        this.description = description;
        this.title = title;
        this.type = type;
        this.links = links;
    }

    @Override
    public String getDescription() { return description; }

    @Override
    public JSONErrorType getType() { return type; }

    @Override
    public HashMap getLinks() { return links; }

    @Override
    public String getTitle() { return title; }
}
