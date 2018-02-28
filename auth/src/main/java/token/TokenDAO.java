package token;

import com.querydsl.jpa.impl.JPAQueryFactory;
import data.AbstractDAO;
import data.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

class TokenDAO extends AbstractDAO {
    public AccessToken persist(AccessToken token) {
        return persistOne(token);
    }

    public RefreshToken persist(RefreshToken token) {
        return persistOne(token);
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
