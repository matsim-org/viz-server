package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
class FeatureCollection {

	private String type;
	private String name;
	private Crs crs;
	private List<Feature> features = new ArrayList<>();

	private FeatureCollection(String type, String name, Crs crs) {
		this.type = type;
		this.name = name;
		this.crs = crs;
	}

	private void addFeature(Feature feature) {
		this.features.add(feature);
	}

	FeatureCollection transformCollection(CoordinateReferenceSystem toCRS) throws FactoryException, TransformException {

		if (!crs.getType().equals("name")) {
			throw new RuntimeException("only geoJson crs of type name is supported. https://geojson.org/geojson-spec.html#coordinate-reference-system-objects");
		}
		CoordinateReferenceSystem fromCRS = CRS.decode(crs.getProperties().get("name"));
		MathTransform transformation = CRS.findMathTransform(fromCRS, toCRS, true);
		FeatureCollection result = new FeatureCollection(type, name, Crs.create(toCRS));
		for (Feature feature : features) {
			result.addFeature(feature.transformGeometry(transformation));
		}
		return result;
	}
}
