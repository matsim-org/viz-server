package org.matsim.viz.files.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"project_id", "tagSummary", "userFileName"}),
                @UniqueConstraint(columnNames = {"project_id", "persistedFileName"})})
public class FileEntry extends Taggable {

    @Column(nullable = false)
    private String userFileName;
    private String contentType;
    private long sizeInBytes;

    @JsonIgnore
    @Column(nullable = false)
    private String persistedFileName;

    @JsonIgnore
    private StorageType storageType = StorageType.Local;

    @JsonIgnore
    @OneToOne(mappedBy = "fileEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PendingFileTransfer pendingFileTransfer;

    @ManyToOne(optional = false)
    private Project project;

    public enum StorageType {Local, S3}
}
