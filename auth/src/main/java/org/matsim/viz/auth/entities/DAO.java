package org.matsim.viz.auth.entities;

import org.matsim.viz.database.PersistenceUnit;

public abstract class DAO {

    protected static PersistenceUnit database = new PersistenceUnit("org.matsim.matsim-webvis.auth");

}
