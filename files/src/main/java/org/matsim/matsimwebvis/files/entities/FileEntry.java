package org.matsim.matsimwebvis.files.entities;

import database.AbstractEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "fileName"})})
public class FileEntry extends AbstractEntity {

    private String fileName;

    @ManyToOne(optional = false)
    private Project project;
}
