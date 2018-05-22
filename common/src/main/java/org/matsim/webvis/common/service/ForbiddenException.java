package org.matsim.webvis.common.service;

public class ForbiddenException extends CodedException {
    public ForbiddenException(String message) {
        super(Error.FORBIDDEN, message);
    }
}
