package relyingParty;

import entities.Client;
import entities.RedirectUri;
import entities.RelyingParty;
import entities.RelyingPartyCredential;
import helper.SecretHelper;

import java.net.URI;

public class RelyingPartyService {

    private RelyingPartyDAO relyingPartyDAO = new RelyingPartyDAO();

    public Client persistNewClient(Client client) throws Exception {

        if (client.getRedirectUris().size() < 1) {
            throw new Exception("relyingParty must have at least one redirect_uri.");
        }

        client.getRedirectUris().forEach(uri -> uri.setClient(client));

        persistNewRelyingParty(client);
        return client;
    }

    public RelyingParty persistNewRelyingParty(RelyingParty party) {
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);
        return relyingPartyDAO.persist(credential).getRelyingParty();
    }

    public Client createClient(String name, Iterable<URI> redirectUris) throws Exception {

        Client client = new Client();
        client.setName(name);

        for (URI uri : redirectUris) {
            RedirectUri redirectUri = new RedirectUri();
            redirectUri.setUri(uri.toString());
            client.getRedirectUris().add(redirectUri);
        }

        return persistNewClient(client);
    }

    public RelyingParty validateRelyingParty(String clientId, String secret) throws Exception {
        RelyingPartyCredential credential = relyingPartyDAO.findCredential(clientId);

        if (credential == null) throw new Exception("client not found");
        if (!SecretHelper.doSecretsMatch(credential.getSecret(), secret))
            throw new Exception("secret did not match");

        return credential.getRelyingParty();
    }

    public Client findClient(String clientId) {
        return relyingPartyDAO.findClient(clientId);
    }


}
