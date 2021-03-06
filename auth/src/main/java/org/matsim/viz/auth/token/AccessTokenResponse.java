package org.matsim.viz.auth.token;

import lombok.Getter;
import org.matsim.viz.auth.entities.Token;

@Getter
class AccessTokenResponse {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String scope;

    AccessTokenResponse(Token token) {
        this.access_token = token.getTokenValue();
        this.token_type = "Bearer";
        this.expires_in = token.getExpiresAt().getEpochSecond();
        this.scope = token.getScope();
    }
}
