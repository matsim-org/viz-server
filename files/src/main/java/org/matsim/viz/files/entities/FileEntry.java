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
        {@UniqueConstraint(columnNames = {"project_id", "userFileName"}),
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

    public enum StorageType {Local, S3}
}
