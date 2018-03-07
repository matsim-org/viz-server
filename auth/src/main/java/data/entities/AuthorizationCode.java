package data.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class AuthorizationCode extends Token {

    @ManyToOne
    @JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "CLIENT_AUTHCODE_FK"))
    private Client client;
}
