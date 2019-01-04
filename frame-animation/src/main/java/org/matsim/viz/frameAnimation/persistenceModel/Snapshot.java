package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Snapshot extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visualization_id")
    private Visualization visualization;
    private double timestep;
    @Lob
    private byte[] data;
}
