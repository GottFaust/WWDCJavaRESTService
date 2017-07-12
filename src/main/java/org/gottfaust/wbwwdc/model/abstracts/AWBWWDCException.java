package org.gottfaust.wbwwdc.model.abstracts;

public abstract class AWBWWDCException extends Exception {

    /** The self link **/
    private String self;

    /**
     * CTOR
     * @param self
     * @param message
     */
    public AWBWWDCException(String self, String message) {
        super(message);
        this.self = self;
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public AWBWWDCException(String self, String message, Throwable throwable){
        super(message, throwable);
        this.self = self;
    }

    /** Get Methods **/
    public String getSelf() {
        return self;
    }
}
