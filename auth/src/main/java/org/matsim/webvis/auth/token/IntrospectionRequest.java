package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.matsim.webvis.common.auth.BasicAuthentication;
import org.matsim.webvis.common.auth.PrincipalCredentialToken;
import org.matsim.webvis.common.communication.RequestWithParams;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import spark.Request;

@Getter
class IntrospectionRequest extends RequestWithParams {

    private String token;
    private PrincipalCredentialToken authentication;

    IntrospectionRequest(Request request) throws InvalidInputException {

        authentication = BasicAuthentication.decodeAuthorizationHeader(request.headers(BasicAuthentication.HEADER_AUTHORIZATION));
        token = extractRequiredValue(OAuthParameters.TOKEN, request.queryMap());
    }
}
