package org.gottfaust.WWDC.model.exceptions;

import org.gottfaust.WWDC.model.abstracts.AWWDCException;

public class WWDCDatabaseException extends AWWDCException {

    /**
     * CTOR
     * @param self
     * @param message
     */
    public WWDCDatabaseException(String self, String message) {
        super(self, message);
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public WWDCDatabaseException(String self, String message, Throwable throwable) {
        super(self, message, throwable);
    }
}
