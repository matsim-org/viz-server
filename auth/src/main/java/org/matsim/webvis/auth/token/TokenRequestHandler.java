package org.matsim.webvis.auth.token;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonResponseHandler;
import org.matsim.webvis.common.service.InternalException;
import spark.Request;
import spark.Response;

public class TokenRequestHandler extends JsonResponseHandler {

    @Override
    protected Answer process(Request request, Response response) {

        TokenRequest tokenRequest = new TokenRequest(request);

        switch (tokenRequest.getGrantType()) {
            case OAuthParameters.GRANT_TYPE_PASSWORD:
                return new PasswordGrant().processRequest(tokenRequest);
            case OAuthParameters.GRANT_TYPE_CLIENT_CREDENTIALS:
                return new ClientCredentialsGrant().processRequest(tokenRequest);
            default:
                throw new InternalException("unsupported grant type");
        }
    }
}
