package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.matsim.webvis.auth.entities.Token;

@Getter
abstract class IntrospectionResponse {

    private boolean active;
    private String scope;
    private String client_id;
    private String username;
    private String token_type;
    private long exp;
    private long iat;
    private String sub;
    private String aud;
    private String iss;

    IntrospectionResponse(Token token, boolean active) {

        this.active = active;
        if (token != null) {
            iat = token.getCreatedAt().toEpochMilli();
            exp = token.getExpiresAt().toEpochMilli();
            token_type = "Bearer";
            sub = token.getSubjectId();
        }
    }
}
