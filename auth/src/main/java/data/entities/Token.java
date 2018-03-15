package data.entities;

import data.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class Token extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "TOKEN_USER_FK"))
    User user;

    @Column(length = 10000)
    String token;
    String tokenType;
    boolean consumed;
    Instant createdAt = Instant.now();
    Instant expiresAt;

}
