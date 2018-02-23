package data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class User extends AbstractEntity {

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String eMail;
}
