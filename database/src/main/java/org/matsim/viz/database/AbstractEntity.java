package org.matsim.viz.database;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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
    @GenericGenerator(name = "uuid", strategy = "org.matsim.viz.database.UUIDGenerator")
    private String id;

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;

        if (o instanceof AbstractEntity) {
            AbstractEntity other = (AbstractEntity) o;
            if (this.hasId() && other.hasId())
                return this.getId().equals(other.getId());
            else
                return super.equals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {

        if (this.getId() != null)
            return this.getId().hashCode();

        return super.hashCode();
    }

    public boolean hasId() {
        return StringUtils.isNotBlank(id);
    }
}
