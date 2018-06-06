package org.matsim.webvis.auth.token;

import lombok.Getter;

@Getter
abstract class GrantRequest {

    private TokenRequest tokenRequest;

    GrantRequest(TokenRequest request) {
        tokenRequest = request;
    }
}
