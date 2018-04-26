package org.matsim.webvis.common.communication;

import org.matsim.webvis.common.service.CodedException;

public class RequestException extends CodedException {

    public RequestException(String errorCode, String message) {
        super(errorCode, message);
    }
}
