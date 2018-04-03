package org.matsim.webvis.files.project;

import lombok.Getter;

@Getter
class CreateProjectRequest {

    private String name = "";

    public CreateProjectRequest() {
    }

    CreateProjectRequest(String name) {
        this.name = name;
    }
}
