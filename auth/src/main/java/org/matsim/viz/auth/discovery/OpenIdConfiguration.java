package org.matsim.viz.auth.discovery;

import lombok.Getter;

import java.net.URI;

@Getter
class OpenIdConfiguration {

    private static final String authorizationPath = "/authorize";
    private static final String tokenPath = "/token";
    private static final String jwksPath = "/certificates";

    // specify relevant endpoints
    private final URI issuer;
    private final URI authorization_endpoint;
    private final URI token_endpoint;
    private final URI jwks_uri;

    private final String[] response_types_supported = new String[]{"token", "id_token", "token id_token"};
    private final String[] id_token_signing_alg_values_supported = new String[]{"RS256", "RS512"};
    private final String[] scopes_supported = new String[]{"openid", "user-client", "service-client"};
    private final String[] token_endpoint_auth_methods_supported = new String[]{"client_secret_basic"};

    OpenIdConfiguration(URI host) {
        this.issuer = host;
        this.authorization_endpoint = host.resolve(authorizationPath);
        this.token_endpoint = host.resolve(tokenPath);
        this.jwks_uri = host.resolve(jwksPath);
    }
}
