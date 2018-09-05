package org.matsim.webvis.files.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.file.LocalRepository;
import org.matsim.webvis.files.file.Repository;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("local")
public class LocalRepositoryFactory implements RepositoryFactory {

    private String uploadDirectory;

    @Override
    public Repository createRepository(PersistenceUnit persistenceUnit) {

        return new LocalRepository(uploadDirectory);
    }
}
