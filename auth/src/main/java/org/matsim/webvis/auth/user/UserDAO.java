package org.matsim.webvis.auth.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.matsim.webvis.auth.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class UserDAO extends DAO {

    public UserCredentials persistCredentials(UserCredentials credentials) {

        if (credentials.getUser().getId() == null)
            return database.persistOne(credentials);

        EntityManager manager = database.getEntityManager();
        manager.getTransaction().begin();
        User persisted = manager.merge(credentials.getUser());
        credentials.setUser(persisted);
        manager.persist(credentials);
        manager.getTransaction().commit();
        manager.close();
        return credentials;
    }

    public UserCredentials findUserCredentials(String eMail) {
        QUserCredentials credentials = QUserCredentials.userCredentials;
        return database.executeQuery(query -> query.selectFrom(credentials)
                .where(credentials.user.eMail.eq(eMail))
                .fetchOne());
    }

    public User findUser(String id) {
        QUser user = QUser.user;
        return database.executeQuery(query -> query.selectFrom(user)
                .where(user.id.eq(id))
                .fetchOne());
    }

    public void deleteUser(User user) {
        QUserCredentials userCredentials = QUserCredentials.userCredentials;
        UserCredentials credentials = database.executeQuery(query -> query.selectFrom(userCredentials)
                .where(userCredentials.user.eq(user))
                .fetchFirst());
        database.removeOne(credentials);
    }

    public List<User> getAllUser() {
        QUser user = QUser.user;
        return database.executeQuery(query -> query.selectFrom(user).fetch());
    }

    public void removeAllUsers() {
        EntityManager em = database.getEntityManager();
        em.getTransaction().begin();
        JPAQueryFactory query = database.createQuery(em);

        List<UserCredentials> credentials = query.selectFrom(QUserCredentials.userCredentials).fetch();

        credentials.forEach(em::remove);
        em.getTransaction().commit();
        em.close();
    }
}
