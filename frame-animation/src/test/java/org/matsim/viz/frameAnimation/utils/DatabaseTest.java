package org.matsim.viz.frameAnimation.utils;

import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Rule;
import org.matsim.viz.frameAnimation.persistenceModel.*;

public class DatabaseTest {

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
            .addEntityClass(MatsimNetwork.class)
            .addEntityClass(Visualization.class)
            .addEntityClass(Snapshot.class)
            .addEntityClass(Plan.class)
            .addEntityClass(Agent.class)
            .addEntityClass(Permission.class)
            .setShowSql(true)
            .build();
}
