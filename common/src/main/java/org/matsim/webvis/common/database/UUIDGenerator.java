package org.matsim.webvis.common.database;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class UUIDGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {

        String result;
        if (o instanceof AbstractEntity) {
            AbstractEntity entity = (AbstractEntity) o;
            if (entity.getId() == null || entity.getId().isEmpty()) {
                result = UUID.randomUUID().toString();
            } else {
                result = entity.getId();
            }
        } else {
            result = UUID.randomUUID().toString();
        }
        return result;
    }
}
