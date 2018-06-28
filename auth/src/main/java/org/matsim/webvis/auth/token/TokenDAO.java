package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.DAO;
import org.matsim.webvis.auth.entities.QToken;
import org.matsim.webvis.auth.entities.Token;

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

    void removeAllTokens() {
        QToken token = QToken.token;
        database.executeTransactionalQuery(query -> query.delete(token).execute());
    }
}
