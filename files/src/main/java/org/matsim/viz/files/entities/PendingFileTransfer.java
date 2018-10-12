package org.matsim.viz.files.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PendingFileTransfer extends AbstractEntity {

    private FileEntry.StorageType toStorage;
    private Status status = Status.Pending;
    @OneToOne(fetch = FetchType.LAZY)
    private FileEntry fileEntry;

    public enum Status {Pending, Transferring, Failed}
}
