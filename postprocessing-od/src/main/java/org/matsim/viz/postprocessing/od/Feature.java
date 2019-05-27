package org.matsim.viz.postprocessing.od;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Feature {

	private Optional<String> id;
	private String type;
	private Map<String, String> properties;
	private Geometry geometry;

	Feature transformGeometry(MathTransform transform) throws TransformException {

		return new Feature(
				this.id, this.type, this.properties, JTS.transform(this.geometry, transform)
		);
	}
}
