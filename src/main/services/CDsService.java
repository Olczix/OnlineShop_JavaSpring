package net.stawrul.services;

import net.stawrul.model.CD;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na płytach.
 */
@Service
public class CDsService extends EntityService<CD> {

    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public CDsService(EntityManager em) {

        //CD.class - klasa encyjna, na której będą wykonywane operacje
        //CD::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, CD.class, CD::getId);
    }

    /**
     * Pobranie wszystkich płyt z bazy danych.
     *
     * @return lista płyt
     */
    public List<CD> findAll() {
        //pobranie listy wszystkich płyt za pomocą zapytania nazwanego (ang. named query)
        //zapytanie jest zdefiniowane w klasie CD
        return em.createNamedQuery(CD.FIND_ALL, CD.class).getResultList();
    }

}
