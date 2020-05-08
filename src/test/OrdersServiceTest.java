package net.stawrul;

import net.stawrul.model.Book;
import net.stawrul.model.CD;
import net.stawrul.model.Film;
import net.stawrul.model.Order;
import net.stawrul.services.OrdersService;
import net.stawrul.services.exceptions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {

    @Mock
    EntityManager em;

    @Test(expected = OutOfStockException.class)
    public void whenOrderedBookNotAvailable_placeOrderThrowsOutOfStockEx() {
        //Arrange
        Order order = new Order();
        Book book = new Book();
        book.setAmount(0);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        //przekazanie mocka do testowanego obiektu
        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = OutOfStockException.class)
    public void whenOrderedFilmNotAvailable_placeOrderThrowsOutOfStockEx() {
        //Arrange
        Order order = new Order();
        Film film = new Film();
        film.setAmount(0);
        order.getFilms().add(film);

        Mockito.when(em.find(Film.class, film.getId())).thenReturn(film);

        //przekazanie mocka do testowanego obiektu
        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = OutOfStockException.class)
    public void whenOrderedCDsNotAvailable_placeOrderThrowsOutOfStockEx() {
        //Arrange
        Order order = new Order();
        CD cd = new CD();
        cd.setAmount(0);
        order.getCds().add(cd);

        Mockito.when(em.find(CD.class, cd.getId())).thenReturn(cd);

        //przekazanie mocka do testowanego obiektu
        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test
    public void whenOrderedBookAvailable_placeOrderDecreasesAmountByOne() {
        //Arrange
        Order order = new Order();
        Book book = new Book();
        book.setAmount(1);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert
        //dostępna liczba książek zmniejszyła się:
        assertEquals(0, (int)book.getAmount());
        //nastąpiło dokładnie jedno wywołanie em.persist(order) w celu zapisania zamówienia:
        Mockito.verify(em, times(1)).persist(order);
    }

    @Test
    public void whenOrderedFilmAvailable_placeOrderDecreasesAmountByOne() {
        //Arrange
        Order order = new Order();
        Film film = new Film();
        film.setAmount(5);
        order.getFilms().add(film);

        Mockito.when(em.find(Film.class, film.getId())).thenReturn(film);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert
        assertEquals(4, (int)film.getAmount());
        Mockito.verify(em, times(1)).persist(order);
    }

    @Test
    public void whenOrderedCDsAvailable_placeOrderDecreasesAmountByOne() {
        //Arrange
        Order order = new Order();
        CD cd = new CD();
        cd.setAmount(10);
        order.getCds().add(cd);

        Mockito.when(em.find(CD.class, cd.getId())).thenReturn(cd);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert
        assertEquals(9, (int)cd.getAmount());
        Mockito.verify(em, times(1)).persist(order);
    }

    @Test(expected = FilmsAndCdsException.class)
    public void whenCDsAndFilmsAreInTheOrderTogether_placeOrderThrowsFilmsAndCdsException() {
        //Arrange
        Order order = new Order();
        Film film = new Film();
        CD cd = new CD();
        film.setAmount(5);
        cd.setAmount(4);
        order.getFilms().add(film);
        order.getCds().add(cd);

        Mockito.when(em.find(Film.class, film.getId())).thenReturn(film);
        Mockito.when(em.find(CD.class, cd.getId())).thenReturn(cd);

        //przekazanie mocka do testowanego obiektu
        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = DuplicateInRequestException.class)
    public void whenTwoBooksInOrder_placeOrderThrowsDuplicateInRequestException() {
        //Arrange
        Order order = new Order();
        Book b1 = new Book();
        b1.setAmount(5);
        order.getBooks().add(b1);
        order.getBooks().add(b1);

        Mockito.when(em.find(Book.class, b1.getId())).thenReturn(b1);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);
        //Assert - exception expected
    }

    @Test(expected = DuplicateInRequestException.class)
    public void whenTwoFilmsInOrder_placeOrderThrowsDuplicateInRequestException() {
        //Arrange
        Order order = new Order();
        Film f = new Film();
        f.setAmount(5);
        order.getFilms().add(f);
        order.getFilms().add(f);

        Mockito.when(em.find(Film.class, f.getId())).thenReturn(f);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);
        //Assert - exception expected
    }

    @Test(expected = DuplicateInRequestException.class)
    public void whenTwoCDsInOrder_placeOrderThrowsDuplicateInRequestException() {
        //Arrange
        Order order = new Order();
        CD cd = new CD();
        cd.setAmount(5);
        order.getCds().add(cd);
        order.getCds().add(cd);

        Mockito.when(em.find(CD.class, cd.getId())).thenReturn(cd);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);
        //Assert - exception expected
    }

    @Test
    public void whenGivenLowercaseString_toUpperReturnsUppercase() {

        //Arrange
        String lower = "abcdef";

        //Act
        String result = lower.toUpperCase();

        //Assert
        assertEquals("ABCDEF", result);
    }
}
