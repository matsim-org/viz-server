package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Crs {

	private String type;
	private Map<String, String> properties = new HashMap<>();
}
