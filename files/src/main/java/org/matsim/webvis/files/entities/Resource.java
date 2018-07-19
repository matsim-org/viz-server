package org.matsim.webvis.files.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.matsim.webvis.database.AbstractEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
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

    public boolean addPermission(Permission permission) {

        if (permission.getId() == null) {
            if (permissions.stream().anyMatch(p -> p.getAgent().equals(permission.getAgent())))
                return false;
        }

        //resource
        permission.setResource(this);
        return permissions.add(permission);
    }

    public void addPermissions(Collection<Permission> permissions) {
        permissions.forEach(this::addPermission);
    }
}
