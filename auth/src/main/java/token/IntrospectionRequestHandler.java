package token;

import communication.AbstractRequestHandler;
import communication.Answer;
import communication.ErrorCode;
import communication.RequestException;
import data.entities.Token;
import relyingParty.RelyingPartyService;
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

        Token token = tokenService.getToken(body.getToken());
        return Answer.ok(new IntrospectionResponse(token));
    }
}
