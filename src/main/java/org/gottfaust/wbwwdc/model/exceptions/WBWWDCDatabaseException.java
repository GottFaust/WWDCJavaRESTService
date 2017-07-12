package org.gottfaust.wbwwdc.model.exceptions;

import org.gottfaust.wbwwdc.model.abstracts.AWBWWDCException;

public class WBWWDCDatabaseException extends AWBWWDCException {

    /**
     * CTOR
     * @param self
     * @param message
     */
    public WBWWDCDatabaseException(String self, String message) {
        super(self, message);
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public WBWWDCDatabaseException(String self, String message, Throwable throwable) {
        super(self, message, throwable);
    }
}
