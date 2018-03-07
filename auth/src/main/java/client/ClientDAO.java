package client;

import data.AbstractDAO;
import data.entities.Client;
import data.entities.QClient;
import data.entities.QRedirectUri;
import data.entities.RedirectUri;

import java.util.List;
import java.util.UUID;

public class ClientDAO extends AbstractDAO {

    public Client persist(Client client) {
        return persistOne(client);
    }

    public Client findClient(UUID clientId) {
        QClient client = QClient.client;
        QRedirectUri uri = QRedirectUri.redirectUri;

        List<RedirectUri> uris = executeQuery(query -> query.selectFrom(uri).fetch());

        return executeQuery(query -> query.selectFrom(client)
                .where(client.id.eq(clientId))
                .leftJoin(client.redirectUris, uri)
                .fetchOne());
    }
}
