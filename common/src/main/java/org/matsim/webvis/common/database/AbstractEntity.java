package org.matsim.webvis.common.database;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.matsim.webvis.common.database.UUIDGenerator")
    private String id;

    public boolean equalId(AbstractEntity entity) {
        /*if (entity == this) return true;

        return entity != null &&
                entity.getId() != null &&
                entity.getId().equals(this.id);*/
        return this.equals(entity);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;

        if (o instanceof AbstractEntity) {
            AbstractEntity other = (AbstractEntity) o;
            if (this.getId() == null || other.getId() == null) {
                return super.equals(other);
            } else {
                return this.getId().equals(other.getId());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {

        if (this.getId() != null)
            return this.getId().hashCode();

        return super.hashCode();
    }
}
