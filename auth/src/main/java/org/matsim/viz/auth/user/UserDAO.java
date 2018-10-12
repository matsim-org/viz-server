package org.matsim.viz.auth.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.matsim.viz.auth.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class UserDAO extends DAO {

    UserCredentials persistCredentials(UserCredentials credentials) {

        if (credentials.getUser().getId() == null)
            return database.persist(credentials);

        EntityManager manager = database.createEntityManager();
        manager.getTransaction().begin();
        User persisted = manager.merge(credentials.getUser());
        credentials.setUser(persisted);
        manager.persist(credentials);
        manager.getTransaction().commit();
        manager.close();
        return credentials;
    }

    UserCredentials findUserCredentials(String eMail) {
        QUserCredentials credentials = QUserCredentials.userCredentials;
        return database.executeQuery(query -> query.selectFrom(credentials)
                .where(credentials.user.eMail.eq(eMail))
                .fetchOne());
    }

    User findUser(String id) {
        QUser user = QUser.user;
        return database.executeQuery(query -> query.selectFrom(user)
                .where(user.id.eq(id))
                .fetchOne());
    }

    void deleteUser(User user) {
        QUserCredentials userCredentials = QUserCredentials.userCredentials;
        UserCredentials credentials = database.executeQuery(query -> query.selectFrom(userCredentials)
                .where(userCredentials.user.eq(user))
                .fetchFirst());
        database.remove(credentials);
    }

    public List<User> getAllUser() {
        QUser user = QUser.user;
        return database.executeQuery(query -> query.selectFrom(user).fetch());
    }

    public void removeAllUsers() {
        EntityManager em = database.createEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = database.createQuery(em);

        List<UserCredentials> credentials = query.selectFrom(QUserCredentials.userCredentials).fetch();

        credentials.forEach(em::remove);
        em.getTransaction().commit();
        em.close();
    }
}
