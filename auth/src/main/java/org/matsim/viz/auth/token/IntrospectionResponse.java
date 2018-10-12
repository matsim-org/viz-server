package org.matsim.viz.auth.token;

import lombok.Getter;
import org.matsim.viz.auth.entities.Token;

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
            this.scope = token.getScope();
        }
    }
}
