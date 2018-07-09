package org.matsim.webvis.files.file;

import org.matsim.webvis.files.entities.Project;

public class RepositoryFactory {


    public ProjectRepository getRepository(Project project) {

        //may return other repository types in the future
        return new DiskProjectRepository(project);
    }
}
