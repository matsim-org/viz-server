package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.matsim.webvis.auth.entities.Token;

@Getter
class IntrospectionResponse {

    private boolean active = true;
    private String scope;
    private String client_id;
    private String username;
    private String token_type;
    private long exp;
    private long iat;
    private String sub;
    private String aud;
    private String iss;

    IntrospectionResponse(Token token) {
        if (token == null) {
            active = false;
            return;
        }
        iat = token.getCreatedAt().toEpochMilli();
        exp = token.getExpiresAt().toEpochMilli();
        token_type = token.getTokenType();
        sub = token.getUser().getId();
    }
}
