package data.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class RelyingParty extends AbstractEntity {

    private String name;
}
