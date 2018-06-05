package org.matsim.webvis.common.errorHandling;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodedException extends RuntimeException {

    private final String errorCode;

    public CodedException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return errorCode + ": " + getMessage();
    }
}
