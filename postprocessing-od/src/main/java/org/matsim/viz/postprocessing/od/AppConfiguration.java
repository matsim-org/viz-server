package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import org.matsim.viz.postprocessing.bundle.PostprocessingConfiguration;

@Getter
public class AppConfiguration extends PostprocessingConfiguration {

	private String geoJsonFiles = "./geoJsonFiles";
}
