package org.matsim.webvis.auth.token;


import lombok.Getter;
import org.matsim.webvis.common.auth.BasicAuthentication;
import org.matsim.webvis.common.auth.PrincipalCredentialToken;
import org.matsim.webvis.common.communication.ContentType;
import org.matsim.webvis.common.communication.RequestWithParams;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import spark.Request;

public class TokenRequest extends RequestWithParams {

    private Request request;

    @Getter
    private String grantType;
    @Getter
    private String scope;
    @Getter
    private PrincipalCredentialToken basicAuth;


    TokenRequest(Request request) {

        if (!ContentType.isFormUrlEncoded(request.contentType())) {
            throw new InvalidInputException("only content type '" + ContentType.FORM_URL_ENCODED + "' allowed");
        }
        basicAuth = BasicAuthentication.decodeAuthorizationHeader(request.headers(BasicAuthentication.HEADER_AUTHORIZATION));
        grantType = extractRequiredValue(OAuthParameters.GRANT_TYPE, request.queryMap());
        scope = extractOptionalValue(OAuthParameters.SCOPE, request.queryMap());
        this.request = request;
    }

    String getRequiredValue(String key) {
        return extractRequiredValue(key, request.queryMap());
    }

    String getOptionalValue(String key) {
        return extractOptionalValue(key, request.queryMap());
    }
}
