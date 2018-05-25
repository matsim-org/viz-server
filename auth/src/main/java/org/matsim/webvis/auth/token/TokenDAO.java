package org.matsim.webvis.auth.token;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.matsim.webvis.auth.entities.DAO;
import org.matsim.webvis.auth.entities.QToken;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;

import javax.persistence.EntityManager;
import java.util.List;

class TokenDAO extends DAO {

    Token persist(Token token) {
        if (token.getId() == null)
            return database.persistOne(token);
        return database.updateOne(token);
    }

    Token find(String tokenId) {
        QToken token = QToken.token;
        return database.executeQuery(query -> query.selectFrom(token)
                .where(token.id.eq(tokenId))
                .fetchOne()
        );
    }

    Token findByTokenValue(String tokenValue) {

        QToken token = QToken.token;
        return database.executeQuery(query -> query.selectFrom(token)
                .where(token.tokenValue.eq(tokenValue)).fetchOne());
    }

    void removeAllTokensForUser(User user) {
        EntityManager em = database.getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = database.createQuery(em);

        QToken token = QToken.token;
        List<Token> tokens = query.selectFrom(token).where(token.subjectId.eq(user.getId())).fetch();
        for (Token t : tokens) {
            em.remove(t);
        }
        em.getTransaction().commit();
        em.close();
    }

    void removeAllTokens() {
        QToken token = QToken.token;
        database.executeTransactionalQuery(query -> query.delete(token).execute());
    }
}
