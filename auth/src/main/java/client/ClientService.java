package client;

import data.entities.Client;
import data.entities.RedirectUri;

import java.net.URI;

public class ClientService {

    private ClientDAO clientDAO = new ClientDAO();

    public Client persistNewClient(Client client) throws Exception {

        if (client.getRedirectUris().size() < 1) {
            throw new Exception("client must have at least one redirect_uri.");
        }

        client.getRedirectUris().forEach(uri -> uri.setClient(client));
        return clientDAO.persist(client);
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

    public Client findClient(String clientId) {
        return clientDAO.findClient(clientId);
    }


}
