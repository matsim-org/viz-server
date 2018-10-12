package org.matsim.webvis.files.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.webvis.database.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"resource_id", "agent_id"})
})

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Permission extends AbstractEntity {

    @ManyToOne(optional = false)
    private Resource resource;

    @ManyToOne
    private Agent agent;

    private Type type;

    public boolean canRead() {
        return type == Type.Read || this.canWrite();
    }

    public boolean canWrite() {
        return type == Type.Write || this.canDelete();
    }

    public boolean canDelete() {
        return type == Type.Delete || this.isOwner();
    }

    public boolean isOwner() {
        return type == Type.Owner;
    }

    public enum Type {Read, Write, Delete, Owner}
}
