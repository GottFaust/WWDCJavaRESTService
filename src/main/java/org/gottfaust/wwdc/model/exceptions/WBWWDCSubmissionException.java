package org.gottfaust.wwdc.model.exceptions;

import org.gottfaust.wwdc.model.abstracts.AwwdcException;

public class wwdcSubmissionException extends AwwdcException {

    /**
     * CTOR
     * @param self
     * @param message
     */
    public wwdcSubmissionException(String self, String message) {
        super(self, message);
    }

    /**
     * CTOR
     * @param self
     * @param message
     * @param throwable
     */
    public wwdcSubmissionException(String self, String message, Throwable throwable) {
        super(self, message, throwable);
    }
}
