package org.matsim.webvis.common.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
class AccessTokenResponse {

    private String access_token;
    private String token_type;
    private String scope;
    private long expires_in;
}
