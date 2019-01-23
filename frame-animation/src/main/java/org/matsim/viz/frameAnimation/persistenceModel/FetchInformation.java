package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class FetchInformation extends AbstractEntity {

    @UpdateTimestamp
    Instant lastFetch = Instant.MIN;
}
