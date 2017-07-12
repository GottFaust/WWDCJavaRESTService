package org.gottfaust.wwdc.model.exceptions;

import org.gottfaust.wwdc.model.abstracts.AwwdcException;

public class wwdcAuthException extends AwwdcException {

    /**
     * CTOR
     * @param self
     * @param message
     */
    public wwdcAuthException(String self, String message) {
        super(self, message);
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public wwdcAuthException(String self, String message, Throwable throwable) {
        super(self, message, throwable);
    }
}
