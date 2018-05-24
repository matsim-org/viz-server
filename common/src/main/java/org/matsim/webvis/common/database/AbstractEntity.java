package org.matsim.webvis.common.database;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.matsim.webvis.common.database.UUIDGenerator")
    private String id;

    public boolean equalId(AbstractEntity entity) {
        if (entity == this) return true;

        return entity != null &&
                entity.getId() != null &&
                entity.getId().equals(this.id);
    }
}
