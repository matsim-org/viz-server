package data;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.function.Function;

public class PersistenceUnit {

    private static final Logger logger = LogManager.getLogger();
    private static PersistenceUnit instance;
    private EntityManagerFactory entityManagerFactory;

    private PersistenceUnit(String unitName) {
        entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
    }

    public static PersistenceUnit getInstance() {
        return instance;
    }

    public static void initializePersistenceUnit(String unitName) {
        instance = new PersistenceUnit(unitName);
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public JPAQueryFactory createQuery(EntityManager manager) {
        return new JPAQueryFactory(manager);
    }

    public <T> T persistOne(T entity) {
        return persistOne(entity, getEntityManager());
    }

    public <T> T persistOne(T entity, EntityManager entityManager) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
        return entity;
    }

    public <T> T updateOne(T entity) {
        return updateOne(entity, getEntityManager());
    }

    public <T> T updateOne(T entity, EntityManager entityManager) {
        entityManager.getTransaction().begin();
        T result = entityManager.merge(entity);
        entityManager.getTransaction().commit();
        entityManager.close();
        return result;
    }

    public <T> void removeOne(T entity) {

        removeOne(entity, getEntityManager());
    }

    public <T> void removeOne(T entity, EntityManager entityManager) {
        entityManager.getTransaction().begin();
        T mergedEntity = entityManager.merge(entity);
        entityManager.remove(mergedEntity);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public <T> void removeMany(List<T> entities) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        for (T entity : entities) {
            em.remove(entity);
        }
        em.getTransaction().commit();
        em.close();
    }

    protected <T> T executeQuery(Function<JPAQueryFactory, T> query) {
        EntityManager em = getEntityManager();
        JPAQueryFactory queryFactory = createQuery(em);
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
