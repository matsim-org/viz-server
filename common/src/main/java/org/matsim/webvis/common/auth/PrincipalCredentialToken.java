package org.matsim.webvis.common.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrincipalCredentialToken {

    private String principal;
    private String credential;
}
