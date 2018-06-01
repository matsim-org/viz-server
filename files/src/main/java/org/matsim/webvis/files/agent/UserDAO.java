package org.matsim.webvis.files.agent;

import org.matsim.webvis.files.entities.QUser;
import org.matsim.webvis.files.entities.User;

public class UserDAO extends AgentDAO {

    User findByIdentityProviderId(String id) {

        QUser user = QUser.user;
        return database.executeQuery(query -> query.selectFrom(user)
                .where(user.authId.eq(id))
                .fetchOne());
    }

    public void removeAllUser() {

        QUser user = QUser.user;
        database.executeTransactionalQuery(query -> query.delete(user).execute());
    }
}
