package org.matsim.viz.postprocessing.bundle;

import io.dropwizard.hibernate.HibernateBundle;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

/**
 * getSessionFactory() from dropwizard-hibernate-bundle can't be called before the bundle's run method was executed
 * This way the the call can be delayed until the session factory is actually needed.
 */
@RequiredArgsConstructor
class LazySessionFactory {

    private final HibernateBundle hibernate;

    SessionFactory getSessionFactory() {
        return hibernate.getSessionFactory();
    }
}
