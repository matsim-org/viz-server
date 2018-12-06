package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.val;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.api.core.v01.network.Network;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertEquals;

public class MatsimNetworkTest {

    private static Network network;

    @BeforeClass
    public static void setUpClass() {
        network = TestUtils.loadTestNetwork();
    }

    @Test
    public void initMatsimNetwork() {

        val result = new MatsimNetwork(network);

        // magic numbers are coming from the test network
        assertEquals(192, result.getData().length); //12 links * 4 values * 4 byte, assuming we have 32 bit floats
        assertEquals(-2500.0, result.getMinEasting(), 0.1);
        assertEquals(-1000.0, result.getMinNorthing(), 0.1);
        assertEquals(1000.0, result.getMaxEasting(), 0.1);
        assertEquals(400.0, result.getMaxNorthing(), 0.1);
    }
}
