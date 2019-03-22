package org.matsim.viz.postprocessing.eventAnimation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.postprocessing.bundle.PersistentVisualization;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Visualization extends PersistentVisualization {

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "visualization", cascade = CascadeType.ALL)
	private MatsimNetwork matsimNetwork;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "visualization")
	private Set<LinkTrip> linkTrips = new HashSet<>();

	private double firstTimestep = Double.MAX_VALUE;
	private double lastTimestep = Double.MIN_VALUE;
	private double minEasting;
	private double maxEasting;
	private double minNorthing;
	private double maxNorthing;

	void addMatsimNetwork(MatsimNetwork network) {
		this.matsimNetwork = network;
		minEasting = network.getMinEasting();
		maxEasting = network.getMaxEasting();
		minNorthing = network.getMinNorthing();
		maxNorthing = network.getMaxNorthing();
		network.setVisualization(this);
	}
}
