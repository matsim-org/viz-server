package org.matsim.webvis.common.service;

import org.matsim.webvis.common.communication.RequestError;

public class InvalidInputException extends CodedException {
    public InvalidInputException(String message) {
        super(RequestError.INVALID_REQUEST, message);
    }
}
