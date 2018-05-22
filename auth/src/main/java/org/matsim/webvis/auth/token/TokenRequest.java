package org.matsim.webvis.auth.token;


import lombok.Getter;
import org.matsim.webvis.common.communication.RequestWithParams;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Request;

@Getter
class TokenRequest extends RequestWithParams {

    private String grantType;
    private String username;
    private String password;

    TokenRequest(Request request) throws InvalidInputException {
        grantType = extractRequiredValue(OAuthParameters.GRANT_TYPE, request.queryMap());
        username = extractRequiredValue(OAuthParameters.USERNAME, request.queryMap());
        password = extractRequiredValue(OAuthParameters.PASSWORD, request.queryMap());
    }
}
