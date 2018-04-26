package org.matsim.webvis.files.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
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
    private Set<FileEntry> files = new HashSet<>();

    public void addFileEntries(Collection<FileEntry> entries) {
        entries.forEach(this::addFileEntry);
    }

    private void addFileEntry(FileEntry entry) {
        files.add(entry);
        entry.setProject(this);
    }

    public void removeFileEntry(FileEntry entry) {
        files.remove(entry);
        entry.setProject(null);
    }
}
