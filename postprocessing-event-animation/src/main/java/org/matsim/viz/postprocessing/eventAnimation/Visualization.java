package org.matsim.viz.postprocessing.eventAnimation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.postprocessing.bundle.PersistentVisualization;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Visualization extends PersistentVisualization {

	@OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "visualization")
	private MatsimNetwork matsimNetwork;

	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "visualization")
	private Set<LinkTrip> linkTrips = new HashSet<>();
}
