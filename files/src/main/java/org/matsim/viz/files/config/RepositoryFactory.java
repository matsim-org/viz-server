package org.matsim.viz.files.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.file.Repository;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface RepositoryFactory extends Discoverable {

    Repository createRepository(PersistenceUnit persistenceUnit);
}
