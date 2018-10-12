package org.matsim.viz.files.entities;

import org.matsim.viz.database.PersistenceUnit;

public class DAO {

    protected PersistenceUnit database;

    public DAO(PersistenceUnit persistenceUnit) {
        database = persistenceUnit;
    }
}
