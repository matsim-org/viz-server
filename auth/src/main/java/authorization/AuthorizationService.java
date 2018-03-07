package authorization;

import client.ClientDAO;
import data.entities.*;
import token.TokenService;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.UUID;

public class AuthorizationService {

    private ClientDAO clientDAO = new ClientDAO();
    private TokenService tokenService = new TokenService();

    AuthorizationService() throws UnsupportedEncodingException {
    }

    public boolean isValidClientInformation(AuthenticationRequest request) {

        boolean isValid = false;
        UUID uuid;
        //check whether client_id is registered
        try {

            uuid = UUID.fromString(request.getClientId());
        } catch (IllegalArgumentException e) {
            return false;
        }
        Client client = clientDAO.findClient(uuid);

        //check whether redirect uri is registered
        if (client != null) {
            String redirectUri = request.getRedirectUri().toString();
            isValid = client.getRedirectUris().stream()
                    .anyMatch(uri -> uri.getUri().equals(redirectUri));
        }

        return isValid;
    }

    public URI generateResponse(AuthenticationRequest request, User user) {

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

        String query = "?code=" + code.getToken();

        if (!request.getState().isEmpty())
            query += "&state=" + request.getState();

        return URI.create(request.getRedirectUri().toString() + query);
    }

    private URI generateAccessResponse(AuthenticationRequest request, User user) {

        String fragment = "#token_type=bearer";

        IdToken idToken = tokenService.createIdToken(user, request.getNonce());
        fragment += "&id_token=" + idToken.getToken();

        if (request.getType().equals(AuthenticationRequest.Type.AccessAndIdToken)) {
            AccessToken accessToken = tokenService.grantAccess(user);
            fragment += "&access_token=" + accessToken.getToken();
        }
        if (!request.getState().isEmpty()) {
            fragment += "&state=" + request.getState();
        }
        return URI.create(request.getRedirectUri().toString() + fragment);
    }


}
