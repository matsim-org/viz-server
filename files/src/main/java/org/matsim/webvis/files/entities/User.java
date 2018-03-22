package org.matsim.webvis.files.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true, exclude = "projects")
public class User extends AbstractEntity {

    @OneToMany(mappedBy = "creator", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Project> projects;
}
