package data;

import lombok.Data;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
class AbstractEntity {

    @Id
    @GeneratedValue
    private long id;
}
