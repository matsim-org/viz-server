package org.matsim.webvis.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class RefreshToken extends Token {

    public RefreshToken() {
        this.tokenType = "";
    }
}
