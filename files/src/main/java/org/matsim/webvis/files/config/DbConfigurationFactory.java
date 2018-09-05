package org.matsim.webvis.files.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import org.matsim.webvis.database.DbConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DbConfigurationFactory extends Discoverable {

    DbConfiguration createConfiguration();
}
