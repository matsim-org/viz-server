package org.matsim.viz.files.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.file.LocalRepository;
import org.matsim.viz.files.file.Repository;
import org.slf4j.LoggerFactory;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("local")
public class LocalRepositoryFactory implements RepositoryFactory {

    private String uploadDirectory;

    @Override
    public Repository createRepository(PersistenceUnit persistenceUnit) {

        LoggerFactory.getLogger(LocalRepositoryFactory.class).info("Creating local repository at: " + uploadDirectory);
        return new LocalRepository(uploadDirectory);
    }
}
