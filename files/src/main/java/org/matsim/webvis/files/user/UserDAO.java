package org.matsim.webvis.files.user;

import org.matsim.webvis.files.entities.DAO;
import org.matsim.webvis.files.entities.QUser;
import org.matsim.webvis.files.entities.User;

public class UserDAO extends DAO {

    public User persist(User user) {

        if (user.getId() == null) {
            return database.persistOne(user);
        }
        return database.updateOne(user);
    }

    public User update(User user) {
        return database.updateOne(user);
    }

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
