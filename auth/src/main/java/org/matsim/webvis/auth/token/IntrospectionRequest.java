package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.matsim.webvis.auth.helper.BasicAuthentication;
import org.matsim.webvis.common.communication.RequestWithParams;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Request;

@Getter
class IntrospectionRequest extends RequestWithParams {

    private String token;
    private BasicAuthentication authentication;

    IntrospectionRequest(Request request) throws InvalidInputException {

        authentication = new BasicAuthentication(request.headers(BasicAuthentication.HEADER_AUTHORIZATION));
        token = extractRequiredValue(OAuthParameters.TOKEN, request.queryMap());
    }
}
