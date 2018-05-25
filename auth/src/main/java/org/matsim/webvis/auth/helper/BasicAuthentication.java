package org.matsim.webvis.auth.helper;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.service.InvalidInputException;

import java.util.Base64;

@Getter
public class BasicAuthentication {

    public static final String HEADER_AUTHORIZATION = "Authorization";

    private String principal;
    private String credential;

    public BasicAuthentication(String authorizationHeader) {
        parseAuthorizationHeader(authorizationHeader);
    }

    private void parseAuthorizationHeader(String header) {
        if (StringUtils.isBlank(header))
            throw new InvalidInputException("No Authorization header");

        String[] args = header.split(" ");
        boolean isValid = false;

        if (args.length == 2 && args[0].equals("Basic")) {
            String[] decoded = new String(Base64.getDecoder().decode(args[1])).split(":");
            if (decoded.length == 2) {
                this.principal = decoded[0];
                this.credential = decoded[1];
                isValid = true;
            }
        }
        if (!isValid)
            throw new InvalidInputException("Invalid Basic Authorization.");
    }
}
