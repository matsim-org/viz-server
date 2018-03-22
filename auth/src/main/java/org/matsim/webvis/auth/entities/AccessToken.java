package org.matsim.webvis.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class AccessToken extends Token {

    @Column(length = 10000)
    private String refreshToken;

    public AccessToken() {
        this.tokenType = "Bearer";
    }
}
