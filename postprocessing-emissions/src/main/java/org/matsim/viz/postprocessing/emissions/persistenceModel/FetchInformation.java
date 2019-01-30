package org.matsim.viz.postprocessing.emissions.persistenceModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class FetchInformation extends AbstractEntity {

    private Instant lastFetch;
}
