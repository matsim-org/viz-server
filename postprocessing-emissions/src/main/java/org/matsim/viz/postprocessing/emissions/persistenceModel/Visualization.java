package org.matsim.viz.postprocessing.emissions.persistenceModel;

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
public class Visualization extends AbstractEntity {

    private Progress progress = Progress.DownloadingInput;

    @Lob
    private String data = "";

    @OneToMany(mappedBy = "visualization", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<>();

    public enum Progress {DownloadingInput, GeneratingData, Done, Failed}
}
