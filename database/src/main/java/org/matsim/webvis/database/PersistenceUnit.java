package org.matsim.webvis.database;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class PersistenceUnit {

    private EntityManagerFactory entityManagerFactory;

    public PersistenceUnit(String unitName) {
        entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
    }

    public EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public JPAQueryFactory createQuery(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    public <T extends AbstractEntity> T persist(T entity) {
        return persist(entity, createEntityManager());
    }

    public <T extends AbstractEntity> T persist(T entity, EntityManager em) {

        em.getTransaction().begin();
        T result = entity;

        if (entity.hasId())
            result = em.merge(entity);
        else
            em.persist(entity);

        em.getTransaction().commit();
        em.close();

        return result;
    }

    public <T extends AbstractEntity> List<T> persistMany(List<T> entities) {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();

        for (T entity : entities)
            if (entity.hasId())
                em.merge(entity);
            else
                em.persist(entity);

        em.getTransaction().commit();
        em.close();
        return entities;
    }

    public <T> void remove(T entity) {
        remove(entity, createEntityManager());
    }

    public <T> void remove(T entity, EntityManager em) {

        em.getTransaction().begin();
        T mergedEntity = em.merge(entity);
        em.remove(mergedEntity);
        em.getTransaction().commit();
        em.close();
    }

    public <T> T executeQuery(Function<JPAQueryFactory, T> query) {
        return executeQuery(query, createEntityManager());
    }

    public <T> T executeQuery(Function<JPAQueryFactory, T> query, EntityManager em) {

        try {
            JPAQueryFactory queryFactory = createQuery(em);
            return query.apply(queryFactory);
        } finally {
            em.close();
        }
    }

    public <T> T executeTransactionalQuery(Function<JPAQueryFactory, T> query) {
        return executeTransactionalQuery(query, createEntityManager());
    }

    public <T> T executeTransactionalQuery(Function<JPAQueryFactory, T> query, EntityManager em) {

        try {
            JPAQueryFactory queryFactory = createQuery(em);
            em.getTransaction().begin();
            T result = query.apply(queryFactory);
            em.getTransaction().commit();
            return result;
        } finally {
            em.close();
        }
    }
}
