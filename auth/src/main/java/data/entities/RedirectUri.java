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
public class RedirectUri extends AbstractEntity {
    private String uri;

    @ManyToOne
    @JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "CLIENT_ID_FK"))
    private Client client;
}
