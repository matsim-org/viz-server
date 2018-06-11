package org.matsim.webvis.files.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"creator_id", "name"})})
public class Project extends Resource {

    private String name;

    @ManyToOne(optional = false)
    private User creator;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<FileEntry> files = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Visualization> visualizations = new HashSet<>();

    public void addFileEntries(Collection<FileEntry> entries) {
        entries.forEach(this::addFileEntry);
    }

    public void addFileEntry(FileEntry entry) {
        files.add(entry);
        entry.setProject(this);
        copyAndAddPermissionsToAddedResource(entry);
    }

    public FileEntry getFileEntry(String id) {
        return files.stream().filter(f -> f.getId().equals(id)).findAny().orElse(null);
    }

    public void removeFileEntry(FileEntry entry) {
        files.remove(entry);
        entry.setProject(null);
    }

    public void addVisualization(Visualization visualization) {
        visualizations.add(visualization);
        visualization.setProject(this);
        copyAndAddPermissionsToAddedResource(visualization);
    }

    public Visualization getVisualization(String id) {
        return visualizations.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    private void copyAndAddPermissionsToAddedResource(Resource resource) {
        for (Permission permission : this.getPermissions())
            resource.addPermission(new Permission(resource, permission.getAgent(), permission.getType()));
    }
}
