package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FeatureCollection {

	private String type;
	private String name;
	private Crs crs;
	private List<Feature> features = new ArrayList<>();
}
