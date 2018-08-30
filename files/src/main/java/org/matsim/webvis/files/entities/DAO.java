package org.matsim.webvis.files.entities;

import org.matsim.webvis.database.PersistenceUnit;

public class DAO {

    protected PersistenceUnit database;

    public DAO(PersistenceUnit persistenceUnit) {
        database = persistenceUnit;
    }
}
