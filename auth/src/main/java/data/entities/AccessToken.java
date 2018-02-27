package data.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class AccessToken extends Token {

    private int expiresIn = 84600;
    private String refreshToken;

    public AccessToken() {
        this.tokenType = "Bearer";
    }
}
