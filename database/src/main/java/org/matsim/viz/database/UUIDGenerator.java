package org.matsim.viz.database;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class UUIDGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {

        if (o instanceof AbstractEntity && StringUtils.isNotBlank(((AbstractEntity) o).getId())) {
            return ((AbstractEntity) o).getId();
        }
        return UUID.randomUUID().toString();
    }
}
