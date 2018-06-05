package org.matsim.webvis.common.errorHandling;

public class InternalException extends CodedException {

    public InternalException(String message) {
        super(Error.UNSPECIFIED_ERROR, message);
    }
}
