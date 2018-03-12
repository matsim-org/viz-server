package data.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class IdToken extends Token {

    public IdToken() {
        this.tokenType = "id_token";
    }
}
