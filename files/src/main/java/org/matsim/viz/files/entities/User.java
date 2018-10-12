package org.matsim.viz.files.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@Entity
public class User extends Agent {

    @OneToMany(mappedBy = "creator", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Project> projects;
}
