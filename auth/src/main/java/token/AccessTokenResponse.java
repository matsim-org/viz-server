package token;

import data.entities.AccessToken;

class AccessTokenResponse {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;

    AccessTokenResponse(AccessToken token) {
        this.access_token = token.getToken();
        this.token_type = token.getTokenType();
        this.expires_in = token.getExpiresAt().getEpochSecond();
        this.refresh_token = token.getRefreshToken();
    }
}
