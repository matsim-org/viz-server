package org.matsim.webvis.auth.authorization;

import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import java.net.URI;

@Getter
class AuthenticationPostRequest extends AuthenticationRequest {

    @NotEmpty
    @FormParam(SCOPE)
    private String scope;

    @NotEmpty
    @FormParam(RESPONSE_TYPE)
    private String responseType;

    @NotNull
    @FormParam(REDIRECT_URI)
    private URI redirectUri;

    @NotEmpty
    @FormParam(CLIENT_ID)
    private String clientId;

    @FormParam(STATE)
    private String state;

    @FormParam(NONCE)
    private String nonce;
}
