package token;

import com.querydsl.jpa.impl.JPAQueryFactory;
import entities.*;

import javax.persistence.EntityManager;
import java.util.List;

class TokenDAO extends DAO {


    public <T extends Token> T persist(T token) {
        return database.persistOne(token);
    }

    public AuthorizationCode persist(AuthorizationCode token, String clientId) {

        EntityManager em = database.getEntityManager();
        em.getTransaction().begin();
        token.setClient(em.getReference(Client.class, clientId));
        em.persist(token);
        em.getTransaction().commit();
        em.close();
        return token;
    }

    public Token find(String tokenValue) {

        QToken token = QToken.token1;
        return database.executeQuery(query -> query.selectFrom(token)
                .where(token.token.eq(tokenValue)).fetchOne());
    }

    public void removeAllTokensForUser(User user) {
        EntityManager em = database.getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = database.createQuery(em);

        QToken token = QToken.token1;
        List<Token> tokens = query.selectFrom(token).where(token.user.eq(user)).fetch();
        for (Token t : tokens) {
            em.remove(t);
        }
        em.getTransaction().commit();
        em.close();
    }
}
