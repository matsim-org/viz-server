package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.AccessToken;
import org.matsim.webvis.common.communication.*;
import org.matsim.webvis.common.service.CodedException;
import spark.Request;
import spark.Response;

public class TokenRequestHandler extends JsonResponseHandler {

    TokenService tokenService = new TokenService();

    public TokenRequestHandler() throws Exception {
    }

    @Override
    protected Answer process(Request request, Response response) {
        if (!ContentType.isFormUrlEncoded(request.contentType())) {
            return Answer.badRequest(RequestError.INVALID_REQUEST, "only content type: " + ContentType.FORM_URL_ENCODED + " allowed");
        }

        TokenRequest tokenRequest;
        try {
            tokenRequest = new TokenRequest(request);
        } catch (RequestException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        }

        if (isGrantTypePassword(tokenRequest)) {
            return processPasswordGrant(tokenRequest);
        }
        return Answer.unsupportedGrantType("api only supports grant_type password.");
    }

    private Answer processPasswordGrant(TokenRequest request) {

        Answer result;

        try {
            AccessToken token =
                    tokenService.grantWithPassword(request.getUsername(), request.getPassword().toCharArray());
            result = Answer.ok(new AccessTokenResponse(token));
        } catch (CodedException e) {
            result = Answer.forbidden(e.getMessage());
        }

        return result;
    }

    private boolean isGrantTypePassword(TokenRequest request) {
        return request.getGrantType().equals(OAuthParameters.GRANT_TYPE_PASSWORD);
    }
}
