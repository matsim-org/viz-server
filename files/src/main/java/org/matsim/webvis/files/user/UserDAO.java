package org.matsim.webvis.files.user;

import org.matsim.webvis.files.entities.DAO;
import org.matsim.webvis.files.entities.QUser;
import org.matsim.webvis.files.entities.User;

public class UserDAO extends DAO {

    public User persistNewUser(User user) {
        return database.persistOne(user);
    }

    public User persist(User user) {
        return database.updateOne(user);
    }

    public void removeAllUser() {

        QUser user = QUser.user;
        database.executeQuery(query -> query.delete(user).execute());
    }
}
