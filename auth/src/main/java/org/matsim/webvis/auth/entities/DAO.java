package org.matsim.webvis.auth.entities;

import org.matsim.webvis.database.PersistenceUnit;

public abstract class DAO {

    protected static PersistenceUnit database = new PersistenceUnit("org.matsim.matsim-webvis.auth");

}
