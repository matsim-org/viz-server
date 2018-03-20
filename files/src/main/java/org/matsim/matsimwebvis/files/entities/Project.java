package org.matsim.matsimwebvis.files.entities;

import database.AbstractEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "files")
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"creator_id", "name"})})
public class Project extends AbstractEntity {

    private String name;

    @ManyToOne(optional = false)
    private User creator;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<FileEntry> files;
}
