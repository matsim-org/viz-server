package org.matsim.webvis.files.communication;

import lombok.Getter;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Request;

@Getter
class AuthenticatedRequest {

    static final String AUTHORIZATION = "Authorization";

    private String token;

    AuthenticatedRequest(Request request) throws InvalidInputException {

        if (hasAuthorization(request)) {
            this.token = extractToken(request.headers(AUTHORIZATION));
        } else
            throw new InvalidInputException("Must specify Authorization header");
    }

    private static String extractToken(String authorizationHeader) throws InvalidInputException {
        String[] content = authorizationHeader.split(" ");
        if (content.length == 2 && content[0].equals("Bearer")) {
            return content[1];
        } else {
            throw new InvalidInputException("Must specify bearer token");
        }
    }

    private boolean hasAuthorization(Request request) {
        return request.headers(AUTHORIZATION) != null && !request.headers(AUTHORIZATION).isEmpty();
    }
}
