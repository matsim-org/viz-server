package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonResponseHandler;
import spark.Request;
import spark.Response;

public class IntrospectionRequestHandler extends JsonResponseHandler {

    RelyingPartyService rpService = RelyingPartyService.Instance;
    TokenService tokenService = TokenService.Instance;

    @Override
    protected Answer process(Request request, Response response) {

        IntrospectionRequest introspection = new IntrospectionRequest(request);
        RelyingParty party = rpService.validateRelyingParty(
                introspection.getAuthentication().getPrincipal(),
                introspection.getAuthentication().getCredential());

        try {
            Token token = tokenService.validateToken(introspection.getToken());
            return Answer.ok(new ActiveIntrospectionResponse(token));
        } catch (RuntimeException e) {
            return Answer.ok(new InactiveIntrospectionResponse());
        }
    }
}
