package org.matsim.webvis.auth.entities;

import org.matsim.webvis.common.database.PersistenceUnit;

public abstract class DAO {

    protected static PersistenceUnit database = new PersistenceUnit("org.matsim.matsim-webvis.auth");

}