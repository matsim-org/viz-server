package org.matsim.matsimwebvis.files.user;

import org.matsim.matsimwebvis.files.entities.DAO;
import org.matsim.matsimwebvis.files.entities.QUser;
import org.matsim.matsimwebvis.files.entities.User;

public class UserDAO extends DAO {

    public User persistUser(User user) {
        return database.persistOne(user);
    }

    public void removeAllUser() {

        QUser user = QUser.user;
        database.executeQuery(query -> query.delete(user).execute());
    }
}
