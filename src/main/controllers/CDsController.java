package net.stawrul.controllers;

import net.stawrul.model.CD;
import net.stawrul.services.CDsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;


/**
 * Kontroler zawierający akcje związane z płytami w sklepie.
 *
 * Parametr "/CDs" w adnotacji @RequestMapping określa prefix dla adresów wszystkich akcji kontrolera.
 */
@RestController
@RequestMapping("/CDs")
public class CDsController {

    //Komponent realizujący logikę biznesową operacji na płytach
    final CDsService cdsService;

    //Instancja klasy CDsService zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public CDsController(CDsService cdsService) {
        this.cdsService = cdsService;
    }

    /**
     * Pobieranie listy wszystkich płyt.
     *
     * Żądanie:
     * GET /CDs
     *
     * @return lista płyt
     */
    @GetMapping
    public List<CD> listCDs() {
        return cdsService.findAll();
    }

    /**
     * Dodawanie nowej płyty.
     *
     * Żądanie:
     * POST /CDs
     *
     * @param cd obiekt zawierający dane nowej płyty, zostanie zbudowany na podstawie danych
     *             przesłanych w ciele żądania (automatyczne mapowanie z formatu JSON na obiekt
     *             klasy CD)
     * @param uriBuilder pomocniczy obiekt do budowania adresu wskazującego na nowo dodaną płytę,
     *                   zostanie wstrzyknięty przez framework Spring
     *
     * @return odpowiedź HTTP dla klienta
     */
    @PostMapping
    public ResponseEntity<Void> addCD(@RequestBody CD cd, UriComponentsBuilder uriBuilder) {

        if (cdsService.find(cd.getId()) == null) {
            //Identyfikator nie istnieje w bazie danych - nowa płyta zostaje zapisana
            cdsService.save(cd);

            //Jeśli zapisywanie się powiodło zwracana jest odpowiedź 201 Created z nagłówkiem Location, który zawiera
            //adres nowo dodanej płyty
            URI location = uriBuilder.path("/CDs/{id}").buildAndExpand(cd.getId()).toUri();
            return ResponseEntity.created(location).build();

        } else {
            //Identyfikator płyty już istnieje w bazie danych. Żądanie POST służy do dodawania nowych elementów,
            //więc zwracana jest odpowiedź z kodem błędu 409 Conflict
            return ResponseEntity.status(CONFLICT).build();
        }
    }

    /**
     * Pobieranie informacji o pojedynczej płycie.
     *
     * Żądanie:
     * GET /CDs/{id}
     *
     * @param id identyfikator płyty
     *
     * @return odpowiedź 200 zawierająca dane płyty lub odpowiedź 404, jeśli płyta o podanym identyfikatorze nie
     * istnieje w bazie danych
     */
    @GetMapping("/{id}")
    public ResponseEntity<CD> getCD(@PathVariable UUID id) {
        //wyszukanie książki w bazie danych
        CD cd = cdsService.find(id);

        //W warstwie biznesowej brak płyty o podanym id jest sygnalizowany wartością null. Jeśli płyta nie została
        //znaleziona zwracana jest odpowiedź 404 Not Found. W przeciwnym razie klient otrzymuje odpowiedź 200 OK
        //zawierającą dane płyty w domyślnym formacie JSON
        return cd != null ? ResponseEntity.ok(cd) : ResponseEntity.notFound().build();
    }

    /**
     * Aktualizacja danych płyty.
     *
     * Żądanie:
     * PUT /CDs/{id}
     *
     * @param cd
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCD(@RequestBody CD cd) {
        if (cdsService.find(cd.getId()) != null) {
            //aktualizacja danych jest możliwa o ile płyta o podanym id istnieje w bazie danych
            cdsService.save(cd);
            return ResponseEntity.ok().build();

        } else {
            //nie odnaleziono książki o podanym id - odpowiedź 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

}

