package data.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Data
class Token {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "TOKEN_USER_FK"))
    User user;
    String token;
    String tokenType;
    boolean consumed;
    Date createdAt = new Date();
}
