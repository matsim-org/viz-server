package org.matsim.webvis.files.project;

import org.matsim.webvis.files.entities.Project;

import java.io.IOException;

public class RepositoryFactory {


    public ProjectRepository getRepository(Project project) throws IOException {

        //may return other repository types in the future
        return new DiskProjectRepository(project);
    }
}
