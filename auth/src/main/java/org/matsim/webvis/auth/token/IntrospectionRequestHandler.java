package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.AccessToken;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonResponseHandler;
import org.matsim.webvis.common.communication.RequestException;
import org.matsim.webvis.common.service.CodedException;
import spark.Request;
import spark.Response;

public class IntrospectionRequestHandler extends JsonResponseHandler {

    RelyingPartyService rpService = new RelyingPartyService();
    TokenService tokenService = new TokenService();

    public IntrospectionRequestHandler() throws Exception {
    }

    @Override
    protected Answer process(Request request, Response response) {

        try {
            IntrospectionRequest introspection = new IntrospectionRequest(request);
            rpService.validateRelyingParty(introspection.getRpId(), introspection.getRpSecret());
            AccessToken token = tokenService.findAccessToken(introspection.getToken());
            return Answer.ok(new IntrospectionResponse(token));

        } catch (RequestException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        } catch (CodedException e) {
            return Answer.internalError(e.getErrorCode(), e.getMessage());
        }
    }
}
