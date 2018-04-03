package org.matsim.webvis.files.user;

import org.matsim.webvis.files.entities.DAO;
import org.matsim.webvis.files.entities.QUser;
import org.matsim.webvis.files.entities.User;

public class UserDAO extends DAO {

    public User persistNewUser(User user) {
        return database.persistOne(user);
    }

    public User update(User user) {
        return database.updateOne(user);
    }

    public User findByIdentityProviderId(String id) {

        QUser user = QUser.user;
        return database.executeQuery(query -> query.selectFrom(user)
                .where(user.authId.eq(id))
                .fetchOne());
    }

    public void removeAllUser() {

        QUser user = QUser.user;
        database.executeQuery(query -> query.delete(user).execute());
    }
}
