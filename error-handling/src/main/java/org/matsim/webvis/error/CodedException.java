package org.matsim.webvis.error;

import lombok.Getter;

@Getter
public class CodedException extends RuntimeException {

    private final String internalErrorCode;
    private final int status;

    public CodedException(int status, String internalErrorCode, String message) {
        super(message);
        this.internalErrorCode = internalErrorCode;
        this.status = status;
    }

    @Override
    public String toString() {
        return this.internalErrorCode + "," + this.getStatus() + ": " + getMessage();
    }
}
