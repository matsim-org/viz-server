package org.matsim.webvis.common.communication;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String error;
    private final String error_description;

    ErrorResponse(String errorCode, String message) {

        this.error = errorCode;
        this.error_description = message;
    }
}
