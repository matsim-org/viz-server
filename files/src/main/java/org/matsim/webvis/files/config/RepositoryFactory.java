package org.matsim.webvis.files.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.file.Repository;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface RepositoryFactory extends Discoverable {

    Repository createRepository(PersistenceUnit persistenceUnit);
}
