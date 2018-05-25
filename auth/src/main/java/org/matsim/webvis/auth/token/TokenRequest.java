package org.matsim.webvis.auth.token;


import lombok.Getter;
import org.matsim.webvis.auth.helper.BasicAuthentication;
import org.matsim.webvis.common.communication.RequestWithParams;
import spark.Request;

public class TokenRequest extends RequestWithParams {

    private Request request;

    @Getter
    private String grantType;
    @Getter
    private BasicAuthentication basicAuth;


    TokenRequest(Request request) {
        basicAuth = new BasicAuthentication(request.headers(BasicAuthentication.HEADER_AUTHORIZATION));
        grantType = extractRequiredValue(OAuthParameters.GRANT_TYPE, request.queryMap());
        this.request = request;
    }

    public String getRequiredValue(String key) {
        return extractRequiredValue(key, request.queryMap());
    }

    public String getOptionalValue(String key) {
        return extractOptionalValue(key, request.queryMap());
    }
}
