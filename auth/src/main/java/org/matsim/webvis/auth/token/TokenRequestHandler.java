package org.matsim.webvis.auth.token;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonResponseHandler;
import org.matsim.webvis.common.service.InternalException;
import spark.Request;
import spark.Response;

public class TokenRequestHandler extends JsonResponseHandler {

    PasswordGrant passwordGrant = new PasswordGrant();
    ClientCredentialsGrant clientCredentialsGrant = new ClientCredentialsGrant();

    @Override
    protected Answer process(Request request, Response response) {

        TokenRequest tokenRequest = new TokenRequest(request);

        switch (tokenRequest.getGrantType()) {
            case OAuthParameters.GRANT_TYPE_PASSWORD:
                return passwordGrant.processRequest(tokenRequest);
            case OAuthParameters.GRANT_TYPE_CLIENT_CREDENTIALS:
                return clientCredentialsGrant.processRequest(tokenRequest);
            default:
                throw new InternalException("unsupported grant type");
        }
    }
}
