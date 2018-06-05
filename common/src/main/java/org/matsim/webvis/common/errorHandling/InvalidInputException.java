package org.matsim.webvis.common.errorHandling;

public class InvalidInputException extends CodedException {
    public InvalidInputException(String message) {
        super(Error.INVALID_REQUEST, message);
    }
}
