package org.matsim.webvis.files.project;

import lombok.Getter;

@Getter
class CreateProjectRequest {

    private String name = "";
    private String userId = "";

    public CreateProjectRequest() {
    }

    CreateProjectRequest(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }
}
