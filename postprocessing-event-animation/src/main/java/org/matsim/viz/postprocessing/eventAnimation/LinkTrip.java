package org.matsim.viz.postprocessing.eventAnimation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
class LinkTrip extends AbstractEntity {

    private double enterTime;
	private double leaveTime;

    private double fromX;
    private double fromY;
    private double toX;
    private double toY;

	@ManyToOne
	private Visualization visualization;
}
