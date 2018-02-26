package user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import data.AbstractDAO;
import data.entities.QUser;
import data.entities.QUserCredentials;
import data.entities.User;
import data.entities.UserCredentials;

import javax.persistence.EntityManager;
import java.util.List;

public class UserDAO extends AbstractDAO {

    public UserCredentials saveCredentials(UserCredentials credentials) {
        return persistOne(credentials);
    }

    public void deleteUser(User user) {
        QUserCredentials userCredentials = QUserCredentials.userCredentials;
        UserCredentials credentials = executeQuery(query -> query.selectFrom(userCredentials).where(userCredentials.user.eq(user)).fetchFirst());
        removeOne(credentials);
    }

    public List<User> getAllUser() {
        QUser user = QUser.user;
        return executeQuery(query -> query.selectFrom(user).fetch());
    }

    public void removeAllUsers() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = getQueryFactory(em);

        List<UserCredentials> credentials = query.selectFrom(QUserCredentials.userCredentials).fetch();

        credentials.forEach(em::remove);
        em.getTransaction().commit();
        em.close();
    }
}
