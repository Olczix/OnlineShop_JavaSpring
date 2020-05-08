package net.stawrul.services;

import net.stawrul.model.Book;
import net.stawrul.model.CD;
import net.stawrul.model.Film;
import net.stawrul.model.Order;
import net.stawrul.services.exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na zamówieniach.
 */
@Service
public class OrdersService extends EntityService<Order> {


    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public OrdersService(EntityManager em) {

        //Order.class - klasa encyjna, na której będą wykonywane operacje
        //Order::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, Order.class, Order::getId);
    }

    /**
     * Pobranie wszystkich zamówień z bazy danych.
     *
     * @return lista zamówień
     */
    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    /**
     * Złożenie zamówienia w sklepie.
     * <p>
     * Zamówienie jest akceptowane, jeśli wszystkie objęte nim produkty są dostępne (przynajmniej 1 sztuka). W wyniku
     * złożenia zamówienia liczba dostępnych sztuk produktów jest zmniejszana o jeden. Metoda działa w sposób
     * transakcyjny - zamówienie jest albo akceptowane w całości albo odrzucane w całości. W razie braku produktu
     * wyrzucany jest wyjątek OutOfStockException.
     *
     * @param order zamówienie do przetworzenia
     */
    @Transactional
    public void placeOrder(Order order) {

        int books = 0, films = 0, cds = 0; //the amount of particular type of product
        List<Book> booksList = new ArrayList<>();
        List<Film> filmsList = new ArrayList<>();
        List<CD> cdsList = new ArrayList<>();

        // we check if any book happen to be duplicate
        // if so - we throw an exception
        // in other case we check if it has more than one copy in the storage, then add the item to our list of already seen items

        for (Book bookStub : order.getBooks()) {
            Book book = em.find(Book.class, bookStub.getId());
            boolean alreadyContainsThisBook = booksList.contains(book);

            if (alreadyContainsThisBook) {
                throw new DuplicateInRequestException();
            } else {
                if (book.getAmount() < 1) {
                    throw new OutOfStockException();
                } else {
                    books++;
                    booksList.add(book);
                }

            }
        }

        for (Film filmStub : order.getFilms()) {
            Film film = em.find(Film.class, filmStub.getId());
            boolean alreadyContainsThisFilm = filmsList.contains(film);

                if (alreadyContainsThisFilm) {
                    throw new DuplicateInRequestException();
                } else {
                    if (film.getAmount() < 1) {
                        throw new OutOfStockException();
                    } else {
                        films++;
                        filmsList.add(film);
                    }
                }
        }

        for (CD cdStub : order.getCds()) {
            CD cd = em.find(CD.class, cdStub.getId());
            boolean alreadyContainsThisCD = cdsList.contains(cd);

            if (alreadyContainsThisCD) {
                throw new DuplicateInRequestException();
            } else {
                if (cd.getAmount() < 1) {
                    throw new OutOfStockException();
                } else {
                    cds++;
                    cdsList.add(cd);
                }
            }
        }

        //System.out.println("ksiazki: " + books + "  filmy: " + films + "  płyty: " + cds);


        //błąd w zamówieniu - zamówiono razem filmy i płtyt CD
        if (films >= 1 && cds >= 1) {
            throw new FilmsAndCdsException();
        }


        //if there was no thrown exception - everything seems to be fine - we confirm the order
        for (Book b : order.getBooks()) {
            Book b2 = em.find(Book.class, b.getId());

            int newAmount = b2.getAmount() - 1;
            b2.setAmount(newAmount);
        }

        for (Film f : order.getFilms()) {
            Film f2 = em.find(Film.class, f.getId());
            int newAmount = f2.getAmount() - 1;
            f2.setAmount(newAmount);
        }

        for (CD c : order.getCds()) {
            CD cd2 = em.find(CD.class, c.getId());
            int newAmount = cd2.getAmount() - 1;
            cd2.setAmount(newAmount);
        }

        //jeśli wcześniej nie został wyrzucony wyjątek OutOfStockException, zamówienie jest zapisywane w bazie danych
        save(order);
    }
}

