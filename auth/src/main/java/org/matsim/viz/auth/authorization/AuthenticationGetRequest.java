package org.matsim.viz.auth.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.ws.rs.QueryParam;
import java.net.URI;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class AuthenticationGetRequest extends AuthenticationRequest {

    @QueryParam(SCOPE)
    private String scope;

    @QueryParam(RESPONSE_TYPE)
    private String responseType;

    @QueryParam(REDIRECT_URI)
    private URI redirectUri;

    @QueryParam(CLIENT_ID)
    private String clientId;

    @QueryParam(STATE)
    private String state;

    @QueryParam(NONCE)
    private String nonce;
}
