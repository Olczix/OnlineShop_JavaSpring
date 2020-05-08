package net.stawrul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Klasa encyjna reprezentująca towar w sklepie (płytę CD).
 */
@Entity
@EqualsAndHashCode(of = "id")
@NamedQueries(value = {
        @NamedQuery(name = CD.FIND_ALL, query = "SELECT cd FROM CD cd")
})
public class CD {
    public static final String FIND_ALL = "CD.FIND_ALL";

    @Getter
    @Id
    UUID id = UUID.randomUUID();

    @Getter
    @Setter
    String title;

    @Getter
    @Setter
    String author;

    @Getter
    @Setter
    Integer yearOfDistribution;

    @Getter
    @Setter
    Integer amount;
}
