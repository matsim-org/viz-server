package org.matsim.webis.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IntrospectionResult {

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

    public IntrospectionResult(boolean isActive, String scope, String subject) {
        this.active = isActive;
        this.scope = scope;
        this.sub = subject;
    }
}
