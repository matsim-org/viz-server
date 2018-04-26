package org.matsim.webvis.common.service;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodedException extends Exception {

    private final String errorCode;

    public CodedException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
