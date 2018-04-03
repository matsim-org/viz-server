package org.matsim.webvis.auth.relyingParty;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.matsim.webvis.auth.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class RelyingPartyDAO extends DAO {

    public RelyingPartyCredential persistCredential(RelyingPartyCredential credential) {

        if (credential.getRelyingParty().getId() == null)
            return database.persistOne(credential);

        EntityManager manager = database.getEntityManager();
        manager.getTransaction().begin();
        RelyingParty party = manager.merge(credential.getRelyingParty());
        credential.setRelyingParty(party);
        manager.persist(credential);
        manager.getTransaction().commit();
        manager.close();
        return credential;
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

    public void removeAllRelyingParties() {

        EntityManager em = database.getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = database.createQuery(em);

        List<RelyingPartyCredential> credentials = query.selectFrom(QRelyingPartyCredential.relyingPartyCredential).fetch();

        credentials.forEach(em::remove);
        em.getTransaction().commit();
        em.close();
    }
}
