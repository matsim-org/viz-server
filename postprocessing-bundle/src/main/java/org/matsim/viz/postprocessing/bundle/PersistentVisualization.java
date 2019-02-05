package org.matsim.viz.postprocessing.bundle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@MappedSuperclass
public class PersistentVisualization extends AbstractEntity {

    @OneToMany(mappedBy = "visualization", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<>();

    private Progress progress = Progress.DownloadingInput;

    public enum Progress {DownloadingInput, GeneratingData, Done, Failed}
}
