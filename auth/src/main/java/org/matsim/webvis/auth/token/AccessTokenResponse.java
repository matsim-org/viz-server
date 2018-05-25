package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.matsim.webvis.auth.entities.Token;

@Getter
class AccessTokenResponse {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;

    AccessTokenResponse(Token token) {
        this.access_token = token.getTokenValue();
        this.token_type = "Bearer";
        this.expires_in = token.getExpiresAt().getEpochSecond();
    }
}
