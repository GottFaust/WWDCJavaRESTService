package org.gottfaust.WWDC.model.exceptions;

import org.gottfaust.WWDC.model.abstracts.AWWDCException;

public class WWDCAuthException extends AWWDCException {

    /**
     * CTOR
     * @param self
     * @param message
     */
    public WWDCAuthException(String self, String message) {
        super(self, message);
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public WWDCAuthException(String self, String message, Throwable throwable) {
        super(self, message, throwable);
    }
}
