package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class Feature {

	private Optional<String> id;
	private String type;
	private Map<String, String> properties;
	private Geometry geometry;
}
