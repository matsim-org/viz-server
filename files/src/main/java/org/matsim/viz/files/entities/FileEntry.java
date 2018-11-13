package org.matsim.viz.files.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"project_id", "tagSummary", "userFileName"}),
                @UniqueConstraint(columnNames = {"project_id", "persistedFileName"})})
public class FileEntry extends Resource {

    private StorageType storageType = StorageType.Local;

    private String userFileName;
    @JsonIgnore
    private String persistedFileName;
    private String contentType;
    private long sizeInBytes;
    @OneToOne(mappedBy = "fileEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PendingFileTransfer pendingFileTransfer;

    @ManyToOne(optional = false)
    private Project project;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tag> tags = new HashSet<>();

    @JsonIgnore
    private String tagSummary;

    public enum StorageType {Local, S3}

    public void addTag(Tag tag) {
        this.tags.add(tag);
        updateTagSummary();
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        updateTagSummary();
    }

    private void updateTagSummary() {
        this.tagSummary = this.tags.stream()
                .sorted((tag1, tag2) -> tag1.getName().compareToIgnoreCase(tag2.getName()))
                .map(Tag::getName)
                .collect(Collectors.joining("."));
    }
}
