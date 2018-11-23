package org.matsim.viz.files.agent;

import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.entities.QUser;
import org.matsim.viz.files.entities.User;

public class UserDAO extends AgentDAO {

    public UserDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    User findUserByIdentityProviderId(String id) {

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
