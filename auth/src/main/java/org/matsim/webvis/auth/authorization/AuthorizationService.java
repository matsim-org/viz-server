package org.matsim.webvis.auth.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.common.errorHandling.CodedException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;

class AuthorizationService {

    static final AuthorizationService Instance = new AuthorizationService();
    TokenService tokenService = TokenService.Instance;

    private static Logger logger = LogManager.getLogger();

    RelyingPartyService relyingPartyService = RelyingPartyService.Instance;

    private AuthorizationService() {

    }

    boolean isValidClientInformation(AuthenticationRequest request) {

        //this is a bit ugly and will be removed as soon as the implicit grant is being refactored
        try {
            relyingPartyService.validateClient(
                    request.getClientId(), request.getRedirectUri(), Arrays.asList(request.getScopes()));
        } catch (CodedException e) {
            return false;
        }
        return true;
    }

    URI generateResponse(AuthenticationRequest request, User user) {

        return generateAccessResponse(request, user);
    }

    private URI generateAccessResponse(AuthenticationRequest request, User user) {

        String fragment = "#token_type=bearer";

        Token idToken = tokenService.createIdToken(user, request.getNonce());
        fragment += "&id_token=" + idToken.getTokenValue();

        logger.info("Issued token to user: " + user.getEMail());

        if (request.getType().equals(AuthenticationRequest.Type.AccessAndIdToken)) {
            Token accessToken = tokenService.grantAccess(user);
            fragment += "&access_token=" + accessToken.getTokenValue();
        }
        if (!request.getState().isEmpty()) {
            fragment += "&state=" + urlEncode(request.getState());
        }
        return URI.create(request.getRedirectUri().toString() + fragment);
    }

    /**
     * in case the os we are running on does not support utf-8...
     *
     * @param toEncode message to encode into url encoding
     * @return urlEncoded string
     */
    private String urlEncode(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
