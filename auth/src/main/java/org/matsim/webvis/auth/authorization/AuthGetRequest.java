package org.matsim.webvis.auth.authorization;

import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import java.net.URI;

@Getter
class AuthGetRequest extends AuthRequest {

    @NotEmpty
    @QueryParam(SCOPE)
    private String scope;

    @NotEmpty
    @QueryParam(RESPONSE_TYPE)
    private String responseType;

    @NotNull
    @QueryParam(REDIRECT_URI)
    private URI redirectUri;

    @NotEmpty
    @QueryParam(CLIENT_ID)
    private String clientId;

    @QueryParam(STATE)
    private String state;

    @QueryParam(NONCE)
    private String nonce;
}
