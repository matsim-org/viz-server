package data.entities;

import data.PersistenceUnit;

public class DAO {

    public static PersistenceUnit dao;

    static {
        PersistenceUnit.initializePersistenceUnit("org.matsim.matsim-webvis.auth");
        dao = PersistenceUnit.getInstance();
    }

}
