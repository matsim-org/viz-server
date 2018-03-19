package user;

import entities.DAO;
import entities.QUser;
import entities.User;

public class UserDAO extends DAO {

    public User persistUser(User user) {
        return database.persistOne(user);
    }

    public void removeAllUser() {

        QUser user = QUser.user;
        database.executeQuery(query -> query.delete(user).execute());
    }
}
