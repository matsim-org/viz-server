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
    private String contentType;
    private long sizeInBytes;

    @JsonIgnore
    private String persistedFileName;

    @JsonIgnore
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

    public void addTags(String[] tagIds) {
        for (String tagId : tagIds) {
            Tag tag = new Tag();
            tag.setId(tagId);
            this.addTag(tag);
        }
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        updateTagSummary();
    }

    private void updateTagSummary() {
        this.tagSummary = this.tags.stream()
                .sorted((tag1, tag2) -> tag1.getId().compareToIgnoreCase(tag2.getId()))
                .map(Tag::getId)
                .collect(Collectors.joining("."));
    }
}
