package org.matsim.webvis.auth.token;

import lombok.Getter;

@Getter
public abstract class GrantRequest {

    private String scope;
    private TokenRequest tokenRequest;

    protected GrantRequest(TokenRequest request) {
        scope = request.getOptionalValue("scope");
        tokenRequest = request;
    }
}
