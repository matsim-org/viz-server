package org.matsim.viz.postprocessing.eventAnimation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class LinkTrip extends AbstractEntity {

	private final double enterTime;
	private final double fromX;
	private final double fromY;
	private final double toX;
	private final double toY;
	private final String vehicleId;
	private final String linkId;
	private double leaveTime;
	@ManyToOne
	private Visualization visualization;
}
