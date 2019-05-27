package org.matsim.viz.postprocessing.od;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Crs {

	private String type;
	private Map<String, String> properties = new HashMap<>();

    static Crs create(CoordinateReferenceSystem crs) {
        HashMap<String, String> properties = new HashMap<>();
        // crs has a singleton collection of identifiers.
        properties.put("name", crs.getIdentifiers().iterator().next().toString());
        return new Crs("name", properties);
    }
}
