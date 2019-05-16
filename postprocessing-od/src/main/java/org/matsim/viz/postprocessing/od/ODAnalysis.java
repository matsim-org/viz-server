/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2018 by the members listed in the COPYING,        *
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

import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.PolylineFeatureFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.core.utils.gis.ShapeFileWriter;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.Time;
import org.opengis.feature.simple.SimpleFeature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 * @author ikaddoura
 */
public final class ODAnalysis {

	private static final Logger log = Logger.getLogger(ODAnalysis.class);
	private final String analysisOutputFolder = "od-analysis/";

	private final String[] helpLegModes;
	private final String stageActivitySubString;

	private final String outputDirectory;
	private final String networkFile;
	private final String shapeFile;
	private final String runId;
	private final String runDirectory;
	private final String zoneId;
	private final List<String> modes;
	private final Coord dummyCoord = new Coord(0.0, 0.0);
	private final Coordinate dummyCoordinateOutside = new Coordinate(4628657, 5803010);
	private final double scaleFactor;
	private final String shapeFileCRS;

	public ODAnalysis(String outputDirectory, String runDirectory, String runId, String shapeFile, String shapeFileCRS, String zoneId, List<String> modes, String[] helpLegModes, String stageActivitySubString, double scaleFactor) {

		if (!outputDirectory.endsWith("/")) outputDirectory = outputDirectory + "/";

		this.outputDirectory = outputDirectory;
		this.shapeFile = shapeFile;
		this.runId = runId;
		this.runDirectory = runDirectory;
		this.zoneId = zoneId;
		this.modes = modes;
		this.helpLegModes = helpLegModes;
		this.stageActivitySubString = stageActivitySubString;
		this.networkFile = null;
		this.scaleFactor = scaleFactor;
		this.shapeFileCRS = shapeFileCRS;
	}

	ODAnalysis(String outputDirectory, String networkFile, String runDirectory, String runId, String shapeFile, String shapeFileCRS, String zoneId, List<String> modes, String[] helpLegModes, String stageActivitySubString, double scaleFactor) {
		if (!outputDirectory.endsWith("/")) outputDirectory = outputDirectory + "/";

		this.outputDirectory = outputDirectory;
		this.shapeFile = shapeFile;
		this.runId = runId;
		this.runDirectory = runDirectory;
		this.zoneId = zoneId;
		this.modes = modes;
		this.helpLegModes = helpLegModes;
		this.stageActivitySubString = stageActivitySubString;
		this.networkFile = networkFile;
		this.scaleFactor = scaleFactor;
		this.shapeFileCRS = shapeFileCRS;
	}

	public void run() throws IOException {

		File file = new File(outputDirectory + analysisOutputFolder);
		file.mkdirs();

		Collection<SimpleFeature> features;
		if (shapeFile.startsWith("http")) {
			URL shapeFileAsURL = new URL(shapeFile);
			features = ShapeFileReader.getAllFeatures(shapeFileAsURL);
		} else {
			features = ShapeFileReader.getAllFeatures(shapeFile);
		}

		Map<String, Geometry> zones = new HashMap<>();

		for (SimpleFeature feature : features) {
			String id = feature.getAttribute(zoneId).toString();
			Geometry geometry = (Geometry) feature.getDefaultGeometry();
			zones.put(id, geometry);
		}

		Network network = ScenarioUtils.createScenario(ConfigUtils.createConfig()).getNetwork();
		MatsimNetworkReader networkReader = new MatsimNetworkReader(network);
		if (networkFile == null) {
			networkReader.readFile(runDirectory + runId + ".output_network.xml.gz");
		} else {
			if (networkFile.startsWith("http")) {
				networkReader.readURL(new URL(networkFile));
			} else {
				networkReader.readFile(networkFile);
			}
		}
		String crsNetwork = (String) network.getAttributes().getAttribute("coordinateReferenceSystem");
		if (!shapeFileCRS.equalsIgnoreCase(crsNetwork)) {
			throw new RuntimeException("Coordinate transformation not yet implemented. Expecting shape file to have the following coordinate reference system: " + crsNetwork);
			// TODO: add coordinate transformation
		}

		EventsManager events = EventsUtils.createEventsManager();

		ODEventAnalysisHandler handler1 = new ODEventAnalysisHandler(helpLegModes, stageActivitySubString);
		events.addHandler(handler1);

		MatsimEventsReader reader = new MatsimEventsReader(events);
		reader.readFile(runDirectory + runId + ".output_events.xml.gz");

		List<ODTrip> odTrips = new ArrayList<>();

		int counter = 0;
		log.info("persons (sample size): " + handler1.getPersonId2tripNumber2departureTime().size());
		for (Id<Person> personId : handler1.getPersonId2tripNumber2departureTime().keySet()) {
			counter++;
			if (counter % 1000 == 0.) log.info("person # " + counter);
			for (Integer tripNr : handler1.getPersonId2tripNumber2departureTime().get(personId).keySet()) {
				ODTrip odTrip = new ODTrip();
				odTrip.setPersonId(personId);
				Id<Link> departureLink = handler1.getPersonId2tripNumber2departureLink().get(personId).get(tripNr);
				Coord departureLinkCoord = network.getLinks().get(departureLink).getCoord();
				odTrip.setOriginCoord(departureLinkCoord);
				odTrip.setOrigin(getDistrictId(zones, departureLinkCoord));

				Coord arrivalLinkCoord;
				if (handler1.getPersonId2tripNumber2arrivalLink().get(personId) == null || handler1.getPersonId2tripNumber2arrivalLink().get(personId).get(tripNr) == null) {
					log.warn("no arrival link for person " + personId + " / trip # " + tripNr + ". Probably a stucking agent.");
					arrivalLinkCoord = dummyCoord;
				} else {
					Id<Link> arrivalLink = handler1.getPersonId2tripNumber2arrivalLink().get(personId).get(tripNr);
					arrivalLinkCoord = network.getLinks().get(arrivalLink).getCoord();
				}
				odTrip.setDestinationCoord(arrivalLinkCoord);
				odTrip.setDestination(getDistrictId(zones, arrivalLinkCoord));
				odTrip.setMode(handler1.getPersonId2tripNumber2legMode().get(personId).get(tripNr));
				odTrip.setDepartureTime(handler1.getPersonId2tripNumber2departureTime().get(personId).get(tripNr));
				odTrips.add(odTrip);
			}
		}

		{
			Map<String, ODRelation> filteredOdRelations = new HashMap<>();
			int filteredTripCounter = 0;
			List<ODTrip> filteredTrips = new ArrayList<>();
			double from = 0.;
			double to = 36 * 3600.;
			TripFilter dayFilter = new TripFilter(from, to, "", modes);
			log.info("###### " + from + " to " + to);
			log.info("total number of trips (sample size): " + odTrips.size());

			for (ODTrip trip : odTrips) {
				if (dayFilter.considerTrip(trip)) {
					filteredTripCounter++;
					filteredTrips.add(trip);
					String od = trip.getOrigin() + "-" + trip.getDestination();
					if (filteredOdRelations.get(od) == null) {
						filteredOdRelations.put(od, new ODRelation(od, trip.getOrigin(), trip.getDestination()));
					} else {
						double tripsSoFar = filteredOdRelations.get(od).getTrips();
						filteredOdRelations.get(od).setTrips(tripsSoFar + 1);
					}
				} else {
					// skip trip
				}
			}
			log.info("filtered trips (sample size): " + filteredTripCounter);
			writeData(filteredOdRelations, zones, outputDirectory + analysisOutputFolder + "od-analysis_DAY_" + modes.toString() + ".csv");
			writeDataTable(filteredOdRelations, outputDirectory + analysisOutputFolder + "od-analysis_DAY_" + modes.toString() + "_from-to-format.csv");
			printODLinesForEachAgent(filteredTrips, outputDirectory + analysisOutputFolder + "trip-od-analysis_DAY_" + modes.toString() + ".shp");

			Map<String, Map<String, ODRelation>> time2odRelation = new HashMap<>();
			time2odRelation.put(from + "-" + to, filteredOdRelations);
			printODLines(time2odRelation, zones, outputDirectory + analysisOutputFolder + "od-analysis_DAY_" + modes.toString() + ".shp");
		}

		{
			Map<String, Map<String, ODRelation>> time2odRelations = new HashMap<>();

			// hourly data, qgis time manager plugin can't handle time >= 24 * 3600.
			for (int hour = 1; hour <= 24; hour++) {
				Map<String, ODRelation> filteredOdRelations = new HashMap<>();
				List<ODTrip> filteredTrips = new ArrayList<>();
				int filteredTripCounter = 0;

				double from = (hour - 1) * 3600.;
				double to = hour * 3600.;

				TripFilter hourFilter = new TripFilter(from, to, "", modes);
				log.info("###### " + from + " to " + to);
				log.info("total number of trips (sample size): " + odTrips.size());

				for (ODTrip trip : odTrips) {
					if (hourFilter.considerTrip(trip)) {
						filteredTripCounter++;
						filteredTrips.add(trip);
						String od = trip.getOrigin() + "-" + trip.getDestination();

						if (filteredOdRelations.get(od) == null) {
							filteredOdRelations.put(od, new ODRelation(od, trip.getOrigin(), trip.getDestination()));
						} else {
							double tripsSoFar = filteredOdRelations.get(od).getTrips();
							filteredOdRelations.get(od).setTrips(tripsSoFar + 1);
						}
					} else {
						// skip trip
					}
				}
				log.info("filtered trips (sample size): " + filteredTripCounter);
				writeData(filteredOdRelations, zones, outputDirectory + analysisOutputFolder + "od-analysis_" + hour + "_" + modes.toString() + ".csv");
				writeDataTable(filteredOdRelations, outputDirectory + analysisOutputFolder + "od-analysis_" + hour + "_" + modes.toString() + "_from-to-format.csv");
				printODLinesForEachAgent(filteredTrips, outputDirectory + analysisOutputFolder + "trip-od-analysis_" + hour + "_" + modes.toString() + ".shp");

				boolean writeHourlyShapefiles = true;

				if (writeHourlyShapefiles) {
					Map<String, Map<String, ODRelation>> time2odRelation = new HashMap<>();
					time2odRelation.put(from + "-" + to, filteredOdRelations);
					printODLines(time2odRelation, zones, outputDirectory + analysisOutputFolder + "od-analysis_" + hour + "_" + modes.toString() + ".shp");
				}

				time2odRelations.put(from + "-" + to, filteredOdRelations);
			}

			printODLines(time2odRelations, zones, outputDirectory + analysisOutputFolder + "od-analysis_HOURLY_" + modes.toString() + ".shp");
		}

	}

	private void writeData(Map<String, ODRelation> odRelations, Map<String, Geometry> zones, String fileName) throws IOException {
		BufferedWriter writer = IOUtils.getBufferedWriter(fileName);
		writer.write("from/to;");

		List<String> zoneIds = new ArrayList<>();
		zoneIds.addAll(zones.keySet());
		zoneIds.add("other");

		for (String zone : zoneIds) {
			writer.write(zone + ";");
		}
		writer.newLine();

		double tripsInMatrixCounter = 0;
		for (String zoneFrom : zoneIds) {
			writer.write(zoneFrom + ";");
			for (String zoneTo : zoneIds) {

				double trips = 0;
				if (odRelations.get(zoneFrom + "-" + zoneTo) != null) {
					trips = odRelations.get(zoneFrom + "-" + zoneTo).getTrips() * this.scaleFactor;
					tripsInMatrixCounter = tripsInMatrixCounter + trips;
				}
				writer.write(trips + ";");
			}
			writer.newLine();
		}

		writer.close();

		log.info("Matrix written to file.");
		log.info("Total number of trips in Matrix: " + tripsInMatrixCounter);
	}

	private void writeDataTable(Map<String, ODRelation> odRelations, String fileName) throws IOException {
		BufferedWriter writer = IOUtils.getBufferedWriter(fileName);
		writer.write("origin;destination;trips");
		writer.newLine();

		for (ODRelation odRelation : odRelations.values()) {
			writer.write(odRelation.getOrigin() + ";" + odRelation.getDestination() + ";" + odRelation.getTrips() * this.scaleFactor);
			writer.newLine();
		}

		writer.close();

		log.info("Table written to file.");
	}

	private String getDistrictId(Map<String, Geometry> districts, Coord coord) {
		Point point = MGC.coord2Point(coord);
		for (String nameDistrict : districts.keySet()) {
			Geometry geo = districts.get(nameDistrict);
			if (geo.contains(point)) {
				return nameDistrict;
			}
		}
		return "other";
	}

	private void printODLines(Map<String, Map<String, ODRelation>> time2odRelations, Map<String, Geometry> zones, String fileName) throws IOException {

		PolylineFeatureFactory factory = new PolylineFeatureFactory.Builder()
				.setCrs(MGC.getCRS(TransformationFactory.DHDN_GK4))
				.setName("OD")
				.addAttribute("OD_ID", String.class)
				.addAttribute("O", String.class)
				.addAttribute("D", String.class)
				.addAttribute("trips", Integer.class)
				.addAttribute("startTime", String.class)
				.addAttribute("endTime", String.class)
				.create();

		Collection<SimpleFeature> features = new ArrayList<SimpleFeature>();

		for (String time : time2odRelations.keySet()) {

			Map<String, ODRelation> odRelations = time2odRelations.get(time);

			for (String od : odRelations.keySet()) {

				if (odRelations.get(od) != null) {

					Coordinate originCoord;
					if (odRelations.get(od).getOrigin().equals("other")) {
						originCoord = dummyCoordinateOutside;
					} else {
						originCoord = zones.get(odRelations.get(od).getOrigin()).getCentroid().getCoordinate();
					}

					Coordinate destinationCoord;
					if (odRelations.get(od).getDestination().equals("other")) {
						destinationCoord = dummyCoordinateOutside;
					} else {
						destinationCoord = zones.get(odRelations.get(od).getDestination()).getCentroid().getCoordinate();
					}

					String[] fromTo = time.split("-");
					double from = Double.valueOf(fromTo[0]);
					double to = Double.valueOf(fromTo[1]);
					SimpleFeature feature = factory.createPolyline(

							new Coordinate[]{
									new Coordinate(originCoord),
									new Coordinate(destinationCoord)}

							, new Object[]{od, odRelations.get(od).getOrigin(), odRelations.get(od).getDestination(), odRelations.get(od).getTrips() * this.scaleFactor, Time.writeTime(from), Time.writeTime(to)}
							, null
					);
					features.add(feature);
				}
			}
		}

		if (!features.isEmpty()) {
			ShapeFileWriter.writeGeometries(features, fileName);
		} else {
			log.warn("Shape file was not written out.");
		}
	}

	private void printODLinesForEachAgent(List<ODTrip> filteredTrips, String fileName) throws IOException {

		PolylineFeatureFactory factory = new PolylineFeatureFactory.Builder()
				.setCrs(MGC.getCRS(TransformationFactory.DHDN_GK4))
				.setName("trip")
				.addAttribute("personId", String.class)
				.addAttribute("O", String.class)
				.addAttribute("D", String.class)
				.addAttribute("depTime", Double.class)
				.create();

		Collection<SimpleFeature> features = new ArrayList<SimpleFeature>();

		for (ODTrip trip : filteredTrips) {

			SimpleFeature feature = factory.createPolyline(

					new Coordinate[]{
							new Coordinate(trip.getOriginCoord().getX(), trip.getOriginCoord().getY()),
							new Coordinate(trip.getDestinationCoord().getX(), trip.getDestinationCoord().getY())}

					, new Object[]{trip.getPersonId(), trip.getOrigin(), trip.getDestination(), trip.getDepartureTime()}
					, null
			);
			features.add(feature);
		}

		if (!features.isEmpty()) {
			ShapeFileWriter.writeGeometries(features, fileName);
		} else {
			log.warn("Shape file was not written out.");
		}
	}

}
