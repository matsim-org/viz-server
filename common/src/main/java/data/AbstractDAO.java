package data;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractDAO {

    private static final Logger logger = LogManager.getLogger();
    private static String persistenceUnitName = "org.matsim.matsim-webvis.auth";
    private static EntityManagerFactory eManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);

    protected EntityManager getEntityManager() {
        return eManagerFactory.createEntityManager();
    }

    protected JPAQueryFactory getQueryFactory(EntityManager manager) {
        return new JPAQueryFactory(manager);
    }

    protected <T> T persistOne(T entity) {
        return persistOne(entity, getEntityManager());
    }

    protected <T> T persistOne(T entity, EntityManager entityManager) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
        return entity;
    }

    protected <T> T updateOne(T entity) {
        return updateOne(entity, getEntityManager());
    }

    protected <T> T updateOne(T entity, EntityManager entityManager) {
        entityManager.getTransaction().begin();
        T result = entityManager.merge(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
    }

    protected <T> void removeOne(T entity) {
        removeOne(entity, getEntityManager());
    }

    protected <T> void removeMany(List<T> entities) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        for (T entity : entities) {
            em.remove(entity);
        }
        em.getTransaction().commit();
        em.close();
    }

    protected <T> void removeOne(T entity, EntityManager entityManager) {
        entityManager.getTransaction().begin();
        T mergedEntity = entityManager.merge(entity);
        entityManager.remove(mergedEntity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    protected <T> T executeQuery(Function<JPAQueryFactory, T> query) {
        EntityManager em = getEntityManager();
        JPAQueryFactory queryFactory = getQueryFactory(em);
        T result = null;
        try {
            result = query.apply(queryFactory);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            em.close();
        }
        return result;
    }
}
