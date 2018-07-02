package org.matsim.webis.oauth;

import lombok.Getter;

@Getter
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
}
