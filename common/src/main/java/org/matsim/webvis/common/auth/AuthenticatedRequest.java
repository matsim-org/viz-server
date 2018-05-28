package org.matsim.webvis.common.auth;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.service.UnauthorizedException;
import spark.Request;

@Getter
class AuthenticatedRequest {

    private String token;

    AuthenticatedRequest(Request request) {

        if (!hasAuthorization(request))
            throw new UnauthorizedException("Must specify Authorization header");

        this.token = extractToken(request.headers(BasicAuthentication.HEADER_AUTHORIZATION));
    }

    private static String extractToken(String authorizationHeader) {
        String[] content = authorizationHeader.split(" ");
        if (content.length == 2 && content[0].equals("Bearer"))
            return content[1];
        else
            throw new UnauthorizedException("Must specify bearer token");
    }

    private boolean hasAuthorization(Request request) {
        return StringUtils.isNotBlank(request.headers(BasicAuthentication.HEADER_AUTHORIZATION));
    }
}
