package org.matsim.viz.frameAnimation.data;


import org.geojson.FeatureCollection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.viz.frameAnimation.utils.TestUtils;

public class PopulationDataTest {

    private PopulationData testObject;
    private Population rawPopulation;

    @Before
    public void setUp() {
        MatsimDataReader reader = new MatsimDataReader(TestUtils.NETWORK_FILE, "", TestUtils.POPULATION_FILE);
        Network network = reader.readNetworkFile();
        reader.setRawNetwork(network);
        rawPopulation = reader.readPopulationFile();
        testObject = new PopulationData(rawPopulation, network);
    }

    @Test
    @Ignore
    public void getPlan() {

        //arrange
        Id id = rawPopulation.getPersons().values().iterator().next().getId();

        //act
        FeatureCollection bla = testObject.getSelectedPlan(id);

        //assert
        String json = bla.toString();
        System.out.println(json);
    }
}
