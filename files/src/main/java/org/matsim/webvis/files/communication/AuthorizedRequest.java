package org.matsim.webvis.files.communication;

import lombok.Getter;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
import spark.Request;

@Getter
class AuthorizedRequest {

    private static final String authorization = "Authorization";

    private String token;

    AuthorizedRequest(Request request) throws RequestException {

        if (hasAuthorization(request)) {
            this.token = extractToken(request.headers(authorization));
        } else
            throw new RequestException(ErrorCode.INVALID_REQUEST, "Must specify authorization header");
    }

    private static String extractToken(String authorizationHeader) throws RequestException {
        String[] content = authorizationHeader.split(" ");
        if (content.length == 2 && content[0].equals("Bearer")) {
            return content[1];
        } else {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "Must specify bearer token");
        }
    }

    private boolean hasAuthorization(Request request) {
        return request.headers(authorization) != null && !request.headers(authorization).isEmpty();
    }
}
