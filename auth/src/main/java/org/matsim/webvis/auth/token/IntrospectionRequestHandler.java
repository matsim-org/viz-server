package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.AccessToken;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.common.communication.AbstractRequestHandler;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
import spark.Request;

public class IntrospectionRequestHandler extends AbstractRequestHandler<IntrospectionRequest> {

    RelyingPartyService rpService = new RelyingPartyService();
    TokenService tokenService = new TokenService();

    public IntrospectionRequestHandler() throws Exception {
        super(IntrospectionRequest.class);
    }

    public IntrospectionRequest parseBody(Request request) throws RequestException {

        return new IntrospectionRequest(request);
    }

    @Override
    protected Answer process(IntrospectionRequest body) {

        try {
            rpService.validateRelyingParty(body.getRpId(), body.getRpSecret());
        } catch (Exception e) {
            return Answer.unauthorized(ErrorCode.INVALID_CLIENT, e.getMessage());
        }

        AccessToken token = tokenService.findAccessToken(body.getToken());
        return Answer.ok(new IntrospectionResponse(token));
    }
}
