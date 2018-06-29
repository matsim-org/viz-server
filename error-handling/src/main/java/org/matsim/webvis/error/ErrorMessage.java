package org.matsim.webvis.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class ErrorMessage {

    private int status;
    private String internalErrorCode;
    private String message;
}
