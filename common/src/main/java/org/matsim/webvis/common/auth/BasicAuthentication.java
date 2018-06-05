package org.matsim.webvis.common.auth;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.HttpCredential;
import org.matsim.webvis.common.errorHandling.InvalidInputException;

import java.util.Base64;

public class BasicAuthentication {

    public static final String HEADER_AUTHORIZATION = "Authorization";

    static String encodeToAuthorizationHeader(PrincipalCredentialToken token) {
        return "Basic " + Base64.getEncoder().encodeToString((token.getPrincipal() + ":" + token.getCredential()).getBytes());
    }

    static HttpCredential encodeToCredential(PrincipalCredentialToken token) {
        return () -> encodeToAuthorizationHeader(token);
    }

    public static PrincipalCredentialToken decodeAuthorizationHeader(String header) {

        if (StringUtils.isBlank(header))
            throw new InvalidInputException("No Authorization header");

        String[] args = header.split(" ");

        if (args.length == 2 && args[0].equals("Basic")) {
            String[] decoded = new String(Base64.getDecoder().decode(args[1])).split(":");
            if (decoded.length == 2) {
                return new PrincipalCredentialToken(decoded[0], decoded[1]);
            }
        }
        throw new InvalidInputException("Invalid Basic Authorization.");
    }
}
