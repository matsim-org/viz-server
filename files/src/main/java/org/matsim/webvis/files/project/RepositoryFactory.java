package org.matsim.webvis.files.project;

import org.matsim.webvis.files.entities.Project;

class RepositoryFactory {


    ProjectRepository getRepository(Project project) {

        //may return other repository types in the future
        return new DiskProjectRepository(project);
    }
}
