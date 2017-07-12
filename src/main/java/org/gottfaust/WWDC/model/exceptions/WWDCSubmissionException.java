package org.gottfaust.WWDC.model.exceptions;

import org.gottfaust.WWDC.model.abstracts.AWWDCException;

public class WWDCSubmissionException extends AWWDCException {

    /**
     * CTOR
     * @param self
     * @param message
     */
    public WWDCSubmissionException(String self, String message) {
        super(self, message);
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public WWDCSubmissionException(String self, String message, Throwable throwable) {
        super(self, message, throwable);
    }
}
