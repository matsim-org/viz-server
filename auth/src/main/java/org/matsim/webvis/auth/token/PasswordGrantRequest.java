package org.matsim.webvis.auth.token;

import lombok.Getter;

@Getter
public class PasswordGrantRequest extends GrantRequest {

    private String username;
    private String password;

    public PasswordGrantRequest(TokenRequest request) {
        super(request);
        this.username = request.getRequiredValue("username");
        this.password = request.getRequiredValue("password");
    }
}
