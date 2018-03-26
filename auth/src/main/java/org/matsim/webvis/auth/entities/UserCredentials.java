package org.matsim.webvis.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class UserCredentials extends AbstractEntity {

    private String password;
    private byte[] salt;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "USER_ID_FK"))
    private User user;
}
