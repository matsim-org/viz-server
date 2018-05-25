package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.common.communication.Answer;

public class PasswordGrant implements Grant {

    TokenService tokenService = TokenService.Instance;

    @Override
    public Answer processRequest(TokenRequest request) {

        PasswordGrantRequest grantRequest = new PasswordGrantRequest(request);
        Token token = tokenService.grantWithPassword(grantRequest.getUsername(), grantRequest.getPassword().toCharArray());
        return Answer.ok(new AccessTokenResponse((token)));
    }
}
