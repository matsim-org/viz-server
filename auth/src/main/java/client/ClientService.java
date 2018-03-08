package client;

import data.entities.Client;
import data.entities.RedirectUri;

import java.net.URI;

public class ClientService {

    private ClientDAO clientDAO = new ClientDAO();

    public Client createClient(String name, URI redirectUri) {

        Client client = new Client();
        client.setName(name);

        RedirectUri uri = new RedirectUri();
        uri.setUri(redirectUri.toString());
        client.getRedirectUris().add(uri);
        uri.setClient(client);

        return clientDAO.persist(client);
    }

    public Client findClient(String clientId) {
        return clientDAO.findClient(clientId);
    }


}
