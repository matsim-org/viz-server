package entities;

import database.PersistenceUnit;

public abstract class DAO {

    protected static PersistenceUnit database = new PersistenceUnit("org.matsim.matsim-webvis.auth");

}
