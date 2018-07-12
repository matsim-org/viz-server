package org.matsim.webis.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Credentials {

    private String principal;
    private String credential;
}
