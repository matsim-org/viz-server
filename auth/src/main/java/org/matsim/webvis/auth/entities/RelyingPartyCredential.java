package org.matsim.webvis.auth.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class RelyingPartyCredential extends AbstractEntity {

    private String secret = UUID.randomUUID().toString();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "relyingparty_id", foreignKey = @ForeignKey(name = "RPCREDENTIAL_RP_FK"))
    private RelyingParty relyingParty;
}
