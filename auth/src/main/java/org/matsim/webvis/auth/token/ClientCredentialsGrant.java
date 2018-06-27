package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.common.communication.Answer;

public class ClientCredentialsGrant implements Grant {

    TokenService tokenService = TokenService.Instance;

    @Override
    public Answer processRequest(TokenRequest request) {

        ClientCredentialsGrantRequest clientRequest = new ClientCredentialsGrantRequest(request);
        Token token = tokenService.grantForScope(clientRequest);
        return Answer.ok(new AccessTokenResponse(token));
    }
}
