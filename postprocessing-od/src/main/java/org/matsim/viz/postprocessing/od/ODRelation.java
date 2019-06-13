/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ikaddoura
 */
@Getter
@NoArgsConstructor
@Setter
@Entity
class ODRelation extends AbstractEntity {

	private int fromIndex = -1;
	private int toIndex = -1;

	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String, Integer> tripsByMode = new HashMap<>();

	@ManyToOne(optional = false)
	private Visualization visualization;

	void accept(ODMeasure measure) {

		if (isFirstAccept()) {
			fromIndex = measure.getFromIndex();
			toIndex = measure.getToIndex();
		} else if (isNotSameFromAndTo(measure))
			throw new RuntimeException("Only collect measures with same origin - destination");

		tripsByMode.merge(measure.getMode(), 1, Integer::sum);
	}

	ODRelation combine(ODRelation relation) {
		if (isFirstAccept()) {
			fromIndex = relation.getFromIndex();
			toIndex = relation.getToIndex();
		} else if (isNotSameFromAndTo(relation))
			throw new RuntimeException("Only collect measures with same origin - destination");

		for (Map.Entry<String, Integer> entry : relation.getTripsByMode().entrySet()) {
			this.tripsByMode.merge(entry.getKey(), entry.getValue(), Integer::sum);
		}
		return this;
	}

	private boolean isFirstAccept() {
		return (fromIndex == -1 && toIndex == -1);
	}

	private boolean isNotSameFromAndTo(ODMeasure measure) {
		return fromIndex != measure.getFromIndex() || toIndex != measure.getToIndex();
	}

	private boolean isNotSameFromAndTo(ODRelation relation) {
		return fromIndex != relation.getFromIndex() || toIndex != relation.getToIndex();
	}
}

