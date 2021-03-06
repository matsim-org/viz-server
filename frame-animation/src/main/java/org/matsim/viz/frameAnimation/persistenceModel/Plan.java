package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Plan extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Visualization visualization;

    @Lob
    private String geoJson;

    private int idIndex;
}
