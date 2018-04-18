package org.matsim.webvis.auth.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.entities.Client;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.token.TokenService;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

class AuthorizationService {

    private static Logger logger = LogManager.getLogger();

    RelyingPartyService relyingPartyService = new RelyingPartyService();
    TokenService tokenService = new TokenService();

    AuthorizationService() throws Exception {
    }

    boolean isValidClientInformation(AuthenticationRequest request) {

        boolean isValid = false;

        //check whether client_id is registered
        Client client = relyingPartyService.findClient(request.getClientId());

        //check whether redirect uri is registered
        if (client != null) {
            String redirectUri = request.getRedirectUri().toString();
            isValid = client.getRedirectUris().stream()
                    .anyMatch(uri -> uri.getUri().equals(redirectUri));
        }

        return isValid;
    }

    URI generateResponse(AuthenticationRequest request, User user) {

        URI result;

        if (request.getType().equals(AuthenticationRequest.Type.AuthCode)) {
            result = generateAuthResponse(request, user);
        } else {
            result = generateAccessResponse(request, user);
        }

        return result;
    }

    private URI generateAuthResponse(AuthenticationRequest request, User user) {

        Token code = tokenService.createAuthorizationCode(user, request.getClientId());
        logger.info("Issued token to user: " + user.getEMail());

        String query = "?code=" + code.getToken();

        if (!request.getState().isEmpty()) {
            query += "&state=" + urlEncode(request.getState());
        }

        return URI.create(request.getRedirectUri().toString() + query);
    }

    private URI generateAccessResponse(AuthenticationRequest request, User user) {

        String fragment = "#token_type=bearer";

        Token idToken = tokenService.createIdToken(user, request.getNonce());
        fragment += "&id_token=" + idToken.getToken();

        logger.info("Issued token to user: " + user.getEMail());

        if (request.getType().equals(AuthenticationRequest.Type.AccessAndIdToken)) {
            Token accessToken = tokenService.grantAccess(user);
            fragment += "&access_token=" + accessToken.getToken();
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
