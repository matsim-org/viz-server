package client;

import data.entities.Client;
import data.entities.RedirectUri;

public class ClientService {

    private ClientDAO clientDAO = new ClientDAO();

    public Client createClient(String name, String redirectUri) {
        Client client = new Client();
        client.setName(name);

        RedirectUri uri = new RedirectUri();
        uri.setUri(redirectUri);
        client.getRedirectUris().add(uri);

        return clientDAO.persist(client);
    }
}
