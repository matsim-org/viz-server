package org.matsim.webvis.auth.authorization;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.auth.entities.Client;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.common.errorHandling.InternalException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

class AuthorizationService {

    static final AuthorizationService Instance = new AuthorizationService();
    private TokenService tokenService = TokenService.Instance;
    private UserService userService = UserService.Instance;

    private RelyingPartyService relyingPartyService = RelyingPartyService.Instance;

    Client validateClient(AuthRequest request) {
        return relyingPartyService.validateClient(
                request.getClientId(), request.getRedirectUri(), request.getScope());
    }

    URI generateAuthenticationResponse(AuthRequest request, String subjectId) {

        String fragment = "#token_type=bearer";
        fragment += getStateIfNecessary(request);
        fragment += getTokens(request, subjectId);
        return URI.create(request.getRedirectUri().toString() + fragment);
    }

    private String getStateIfNecessary(AuthRequest request) {
        if (StringUtils.isNotBlank(request.getState()))
            return "&state=" + urlEncode(request.getState());
        return "";
    }

    private String getTokens(AuthRequest request, String subjectId) {

        User user = userService.findUser(subjectId);
        if (user == null)
            throw new InternalException("could not find user from id-token");

        String result = addIdTokenIfNecessary(request, user);
        result += addAccessTokenIfNecessary(request, user);
        return result;
    }

    private String addIdTokenIfNecessary(AuthRequest request, User user) {

        if (request.isResponseTypeIdToken()) {
            Token idToken = tokenService.createIdToken(user, request.getNonce());
            return "&id_token=" + idToken.getTokenValue();
        }
        return "";
    }

    private String addAccessTokenIfNecessary(AuthRequest request, User user) {
        if (request.isResponseTypeToken()) {

            String scope = request.getScope().replace("openid", "").trim();
            Token accessToken = tokenService.createAccessToken(user, String.join(" ", scope));
            return "&access_token=" + accessToken.getTokenValue();
        }
        return "";
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
            throw new InternalException("Could not encode state");
        }
    }
}
