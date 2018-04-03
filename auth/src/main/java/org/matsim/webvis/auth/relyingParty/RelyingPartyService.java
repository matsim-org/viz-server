package org.matsim.webvis.auth.relyingParty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.config.ConfigClient;
import org.matsim.webvis.auth.config.ConfigRelyingParty;
import org.matsim.webvis.auth.entities.Client;
import org.matsim.webvis.auth.entities.RedirectUri;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.RelyingPartyCredential;
import org.matsim.webvis.auth.helper.SecretHelper;

import java.net.URI;

public class RelyingPartyService {

    private static Logger logger = LogManager.getLogger();

    private RelyingPartyDAO relyingPartyDAO = new RelyingPartyDAO();

    public Client createClient(String name, Iterable<URI> redirectUris) {
        return createClient(name, null, redirectUris);
    }

    public Client createClient(ConfigClient configClient) {
        return createClient(configClient.getName(), configClient.getId(), configClient.getRedirectUris());
    }

    private Client createClient(String name, String id, Iterable<URI> redirectUris) {

        Client client = new Client();
        client.setId(id);
        client.setName(name);
        for (URI uri : redirectUris) {
            RedirectUri redirectUri = new RedirectUri();
            redirectUri.setUri(uri.toString());
            redirectUri.setClient(client);
            client.getRedirectUris().add(redirectUri);
        }
        return (Client) persistNewRelyingParty(client);
    }

    public RelyingParty createRelyingParty(ConfigRelyingParty configParty) {
        RelyingParty party = new RelyingParty();
        party.setName(configParty.getName());
        party.setId(configParty.getId());
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

    public RelyingParty validateRelyingParty(String clientId, String secret) throws Exception {
        RelyingPartyCredential credential = relyingPartyDAO.findCredential(clientId);

        if (credential == null) throw new Exception("client not found");
        if (!SecretHelper.match(credential.getSecret(), secret))
            throw new Exception("secret did not match");

        return credential.getRelyingParty();
    }

    public Client findClient(String clientId) {
        return relyingPartyDAO.findClient(clientId);
    }


}
