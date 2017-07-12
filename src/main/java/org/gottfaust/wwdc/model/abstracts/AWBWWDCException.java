package org.gottfaust.wwdc.model.abstracts;

public abstract class AwwdcException extends Exception {

    /** The self link **/
    private String self;

    /**
     * CTOR
     * @param self
     * @param message
     */
    public AwwdcException(String self, String message) {
        super(message);
        this.self = self;
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public AwwdcException(String self, String message, Throwable throwable){
        super(message, throwable);
        this.self = self;
    }

    /** Get Methods **/
    public String getSelf() {
        return self;
    }
}
