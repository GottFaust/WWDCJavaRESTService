package org.gottfaust.WWDC.model.abstracts;

public abstract class AWWDCException extends Exception {

    /** The self link **/
    private String self;

    /**
     * CTOR
     * @param self
     * @param message
     */
    public AWWDCException(String self, String message) {
        super(message);
        this.self = self;
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public AWWDCException(String self, String message, Throwable throwable){
        super(message, throwable);
        this.self = self;
    }

    /** Get Methods **/
    public String getSelf() {
        return self;
    }
}
