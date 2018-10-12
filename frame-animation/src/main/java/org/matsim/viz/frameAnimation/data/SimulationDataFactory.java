package org.matsim.viz.frameAnimation.data;

class SimulationDataFactory {

    SimulationData createSimulationData(
            String networkFilePath, String eventsFilePath, String populationFilePath, double snapshotPeriod
    ) {
        return new SimulationData(networkFilePath, eventsFilePath, populationFilePath, snapshotPeriod);
    }
}
