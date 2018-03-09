package token;

import com.querydsl.jpa.impl.JPAQueryFactory;
import data.AbstractDAO;
import data.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

class TokenDAO extends AbstractDAO {


    public <T extends Token> T persist(T token) {
        return persistOne(token);
    }

    public AuthorizationCode persist(AuthorizationCode token, String clientId) {

        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        token.setClient(em.getReference(Client.class, clientId));
        em.persist(token);
        em.getTransaction().commit();
        em.close();
        return token;
    }

    public void removeAllTokensForUser(User user) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = getQueryFactory(em);

        QToken token = QToken.token1;
        List<Token> tokens = query.selectFrom(token).where(token.user.eq(user)).fetch();
        for (Token t : tokens) {
            em.remove(t);
        }
        em.getTransaction().commit();
        em.close();
    }
}
