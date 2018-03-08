package client;

import com.querydsl.jpa.impl.JPAQueryFactory;
import data.AbstractDAO;
import data.entities.Client;
import data.entities.QClient;
import data.entities.QRedirectUri;

import javax.persistence.EntityManager;
import java.util.List;

public class ClientDAO extends AbstractDAO {

    public Client persist(Client client) {
        return persistOne(client);
    }

    public Client findClient(String clientId) {
        QClient client = QClient.client;
        QRedirectUri uri = QRedirectUri.redirectUri;

        return executeQuery(query -> query.selectFrom(client)
                .where(client.id.eq(clientId))
                .leftJoin(client.redirectUris, uri).fetchJoin()
                .fetchOne());
    }

    public void removeAllClients() {

        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = getQueryFactory(em);

        List<Client> clients = query.selectFrom(QClient.client).fetch();

        clients.forEach(em::remove);
        em.getTransaction().commit();
        em.close();
    }
}
