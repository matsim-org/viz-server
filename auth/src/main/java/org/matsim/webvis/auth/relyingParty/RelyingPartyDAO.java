package org.matsim.webvis.auth.relyingParty;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.matsim.webvis.auth.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class RelyingPartyDAO extends DAO {

    public RelyingPartyCredential persist(RelyingPartyCredential credentials) {
        return database.persistOne(credentials);
    }

    public Client findClient(String clientId) {
        QClient client = QClient.client;
        QRedirectUri uri = QRedirectUri.redirectUri;

        return database.executeQuery(query -> query.selectFrom(client)
                .where(client.id.eq(clientId))
                .leftJoin(client.redirectUris, uri).fetchJoin()
                .fetchOne());
    }

    public RelyingPartyCredential findCredential(String clientId) {
        QRelyingPartyCredential credential = QRelyingPartyCredential.relyingPartyCredential;
        return database.executeQuery(query -> query.selectFrom(credential)
                .where(credential.relyingParty.id.eq(clientId))
                .fetchOne());
    }

    public void removeAllClients() {

        EntityManager em = database.getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = database.createQuery(em);
        QRelyingPartyCredential credential = QRelyingPartyCredential.relyingPartyCredential;

        List<RelyingPartyCredential> clients = query.selectFrom(credential).fetch();

        clients.forEach(em::remove);
        em.getTransaction().commit();
        em.close();
    }
}
