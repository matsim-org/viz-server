package org.matsim.viz.clientAuth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthenticationResult {

    private String subjectId;
    private String scope;
}
