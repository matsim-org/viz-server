package org.matsim.viz.auth.token;

import org.matsim.viz.auth.entities.DAO;
import org.matsim.viz.auth.entities.QToken;
import org.matsim.viz.auth.entities.Token;

class TokenDAO extends DAO {

    Token persist(Token token) {
        return database.persist(token);
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

    void removeAllTokens() {
        QToken token = QToken.token;
        database.executeTransactionalQuery(query -> query.delete(token).execute());
    }
}
