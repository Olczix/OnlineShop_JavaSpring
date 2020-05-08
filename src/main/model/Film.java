package net.stawrul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Klasa encyjna reprezentujÄ…ca towar w sklepie (film).
 */
@Entity
@EqualsAndHashCode(of = "id")
@NamedQueries(value = {
        @NamedQuery(name = Film.FIND_ALL, query = "SELECT f FROM Film f")
})
public class Film {
    public static final String FIND_ALL = "Film.FIND_ALL";

    @Getter
    @Id
    UUID id = UUID.randomUUID();

    @Getter
    @Setter
    String title;

    @Getter
    @Setter
    String director;

    @Getter
    @Setter
    Integer durationTime;

    @Getter
    @Setter
    Integer amount;
}
