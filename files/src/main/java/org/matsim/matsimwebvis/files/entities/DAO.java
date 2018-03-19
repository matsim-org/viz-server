package org.matsim.matsimwebvis.files.entities;

import database.PersistenceUnit;

public abstract class DAO {

    protected PersistenceUnit database = new PersistenceUnit("org.matsim.matsim-webvis.files");
}
