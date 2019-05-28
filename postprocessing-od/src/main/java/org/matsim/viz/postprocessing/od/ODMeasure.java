package org.matsim.viz.postprocessing.od;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class ODMeasure {

    private String mode;
	private int fromIndex;
	private int toIndex;

	boolean hasValidToAndFromZone() {
		return (fromIndex >= 0 && toIndex >= 0);
	}

	String getFromToKey() {
		return fromIndex + "_" + toIndex;
	}
}
