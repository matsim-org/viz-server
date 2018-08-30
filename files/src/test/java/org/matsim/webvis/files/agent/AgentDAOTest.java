package org.matsim.webvis.files.agent;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.files.entities.PublicAgent;
import org.matsim.webvis.files.entities.ServiceAgent;
import org.matsim.webvis.files.util.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AgentDAOTest {

    private AgentDAO testObject;

    @AfterClass
    public static void tearDownFixture() {

        //the application relies on presence of Public- and ServiceAgent
        TestUtils.getAgentService().initializeSpecialAgents();
    }

    @Before
    public void setUp() {
        testObject = new AgentDAO(TestUtils.getPersistenceUnit());
        testObject.removeAllAgents();
    }

    @Test
    public void findOrCreateServiceAgent_serviceAgentCreated() {

        ServiceAgent agent = testObject.findOrCreateServiceAgent();

        assertNotNull(agent);
    }

    @Test
    public void findOrCreateServiceAgent_alreadyPresent_agentIsFetched() {

        ServiceAgent agent = new ServiceAgent();
        testObject.persist(agent);

        ServiceAgent fetched = testObject.findOrCreateServiceAgent();

        assertEquals(agent, fetched);
    }

    @Test
    public void findOrCreatePublicAgent_publicAgentCreated() {

        PublicAgent agent = testObject.findOrCreatePublicAgent();

        assertNotNull(agent);
    }

    @Test
    public void findOrCreatePublicAgent_alreadyPresent_agentIsFetched() {

        PublicAgent agent = new PublicAgent();
        testObject.persist(agent);

        PublicAgent fetched = testObject.findOrCreatePublicAgent();

        assertEquals(agent, fetched);
    }
}
