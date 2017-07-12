package org.gottfaust.wbwwdc.model.exceptions;

import org.gottfaust.wbwwdc.model.abstracts.AWBWWDCException;

public class WBWWDCAuthException extends AWBWWDCException {

    /**
     * CTOR
     * @param self
     * @param message
     */
    public WBWWDCAuthException(String self, String message) {
        super(self, message);
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public WBWWDCAuthException(String self, String message, Throwable throwable) {
        super(self, message, throwable);
    }
}
