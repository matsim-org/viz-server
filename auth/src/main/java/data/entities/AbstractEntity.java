package data.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class AbstractEntity {

    @Id
    @GeneratedValue
    private long id;
}
