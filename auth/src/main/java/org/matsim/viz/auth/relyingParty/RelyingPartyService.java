package org.matsim.viz.auth.relyingParty;

import lombok.AllArgsConstructor;
import org.matsim.viz.auth.config.ConfigClient;
import org.matsim.viz.auth.config.ConfigRelyingParty;
import org.matsim.viz.auth.entities.Client;
import org.matsim.viz.auth.entities.RedirectUri;
import org.matsim.viz.auth.entities.RelyingParty;
import org.matsim.viz.auth.entities.RelyingPartyCredential;
import org.matsim.viz.auth.helper.SecretHelper;
import org.matsim.viz.error.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
public class RelyingPartyService {

    private static Logger logger = LoggerFactory.getLogger(RelyingPartyService.class);

    private RelyingPartyDAO relyingPartyDAO;

    Client createClient(String name, Iterable<URI> redirectUris) {
        return createClient(name, null, redirectUris, null);
    }

    public Client createClient(ConfigClient configClient) {
        return createClient(configClient.getName(), configClient.getId(), configClient.getRedirectUris(), configClient.getScopes());
    }

    private Client createClient(String name, String id, Iterable<URI> redirectUris, Set<String> scopes) {

        Client client = new Client();
        client.setId(id);
        client.setName(name);
        client.setScopes(scopes);
        for (URI uri : redirectUris) {
            RedirectUri redirectUri = new RedirectUri();
            redirectUri.setUri(uri);
            client.addRedirectUri(redirectUri);
        }
        return (Client) persistNewRelyingParty(client);
    }

    public RelyingParty createRelyingParty(ConfigRelyingParty configParty) {
        RelyingParty party = new RelyingParty();
        party.setName(configParty.getName());
        party.setId(configParty.getId());
        party.setScopes(configParty.getScopes());
        return createCredentialWithNonRandomSecret(party, configParty).getRelyingParty();
    }

    private RelyingPartyCredential createCredentialWithNonRandomSecret(RelyingParty party, ConfigRelyingParty configParty) {
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);
        credential.setSecret(configParty.getSecret());
        return relyingPartyDAO.persistCredential(credential);
    }

    private RelyingParty persistNewRelyingParty(RelyingParty party) {
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);
        RelyingParty persisted = relyingPartyDAO.persistCredential(credential).getRelyingParty();
        logger.info("persisted relying party: (id: " + persisted.getId() + ", secret: " + credential.getSecret());
        return persisted;
    }

    RelyingParty validateRelyingParty(String clientId, String secret, String scope) {

        RelyingParty party = validateRelyingParty(clientId, secret);

        if (scopesDontMatch(party.getScopes(), scope))
            throw new UnauthorizedException("scopes don't match");

        return party;
    }

    RelyingParty validateRelyingParty(String clientId, String secret) {

        RelyingPartyCredential credential = relyingPartyDAO.findCredential(clientId);

        if (credential == null || !SecretHelper.match(credential.getSecret(), secret))
            throw new UnauthorizedException("invalid client id or secret or scope not allowed");

        return credential.getRelyingParty();
    }

    public String validateRelyingPartyScope(RelyingParty rp, String scope) {
        if (scopesDontMatch(rp.getScopes(), scope))
            throw new UnauthorizedException("requested scope is not registered");
        return scope;
    }

    public Client validateClient(String clientId, URI redirectUri, String scope) {

        Client client = relyingPartyDAO.findClient(clientId);

        if (client == null || !clientHasRedirectUri(client, redirectUri))
            throw new UnauthorizedException("Client not found or redirect uri not valid");
        if (scopesDontMatch(client.getScopes(), scope))
            throw new UnauthorizedException("requested scope is not registered");

        return client;
    }

    Client findClient(String clientId) {
        return relyingPartyDAO.findClient(clientId);
    }

    private boolean scopesDontMatch(Collection<String> scopes, String scope) {

        Collection<String> scopesToCompare = Arrays.asList(scope.split(" "));
        return (scopes.size() != scopesToCompare.size())
                || !scopes.stream().allMatch(clientScope -> containsScope(scopesToCompare, clientScope));
    }

    private boolean containsScope(Collection<String> scopes, String scopeToMatch) {
        return scopes.stream().anyMatch(scope -> scope.equals(scopeToMatch));
    }

    private boolean clientHasRedirectUri(Client client, URI uriToTest) {
        return client.getRedirectUris().stream()
                .anyMatch(uri -> uri.getUri().equals(uriToTest));
    }
}
