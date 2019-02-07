package org.matsim.viz.postprocessing.bundle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FetchInformation extends AbstractEntity {

    Timestamp lastFetch = Timestamp.from(Instant.EPOCH);
}
