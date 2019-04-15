package org.matsim.viz.files.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public abstract class Resource extends AbstractEntity {

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Permission> permissions = new HashSet<>();

    @CreationTimestamp
    private Instant createdAt;

	@UpdateTimestamp
	private Instant updatedAt;

    public boolean addPermission(Permission permission) {

        if (permission.getId() == null) {
            if (permissions.stream().anyMatch(p -> p.getAgent().equals(permission.getAgent())))
                return false;
        }

        //resource
        permission.setResource(this);
        return permissions.add(permission);
    }

    private boolean removePermission(Permission permission) {
        return permissions.remove(permission);
    }

    public boolean removePermission(Agent forAgent) {
        return removePermission(permissions.stream().filter(p -> p.getAgent().equals(forAgent)).findFirst().get());
    }
}
