package org.matsim.webvis.auth.token;


import lombok.Getter;
import org.matsim.webvis.common.communication.RequestException;
import org.matsim.webvis.common.communication.RequestWithParams;
import spark.Request;

@Getter
class TokenRequest extends RequestWithParams {

    private String grantType;
    private String username;
    private String password;

    TokenRequest(Request request) throws RequestException {
        grantType = extractRequiredValue(OAuthParameters.GRANT_TYPE, request.queryMap());
        username = extractRequiredValue(OAuthParameters.USERNAME, request.queryMap());
        password = extractRequiredValue(OAuthParameters.PASSWORD, request.queryMap());
    }
}
