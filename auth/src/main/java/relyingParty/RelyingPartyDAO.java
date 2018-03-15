package relyingParty;

import com.querydsl.jpa.impl.JPAQueryFactory;
import data.AbstractDAO;
import data.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class RelyingPartyDAO extends AbstractDAO {

    public RelyingPartyCredential persist(RelyingPartyCredential credentials) {
        return persistOne(credentials);
    }

    public Client findClient(String clientId) {
        QClient client = QClient.client;
        QRedirectUri uri = QRedirectUri.redirectUri;

        return executeQuery(query -> query.selectFrom(client)
                .where(client.id.eq(clientId))
                .leftJoin(client.redirectUris, uri).fetchJoin()
                .fetchOne());
    }

    public RelyingPartyCredential findCredential(String clientId) {
        QRelyingPartyCredential credential = QRelyingPartyCredential.relyingPartyCredential;
        return executeQuery(query -> query.selectFrom(credential)
                .where(credential.relyingParty.id.eq(clientId))
                .fetchOne());
    }

    public void removeAllClients() {

        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = getQueryFactory(em);
        QRelyingPartyCredential credential = QRelyingPartyCredential.relyingPartyCredential;

        List<RelyingPartyCredential> clients = query.selectFrom(credential).fetch();

        clients.forEach(em::remove);
        em.getTransaction().commit();
        em.close();
    }
}
