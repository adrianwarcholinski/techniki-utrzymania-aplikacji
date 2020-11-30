package pl.lodz.p.it.ssbd2020.mor.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link ReservationEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface ReservationFacadeReadCommittedLocal extends BasicFacadeOperations<ReservationEntity> {
    /**
     * Implementacja tej metody powinna zwracać obiekt klasy {@link ReservationEntity} zgodnie z przekazanym parametrem.
     *
     * @param reservationNumber numer rezerwacji.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<ReservationEntity> findByReservationNumber(long reservationNumber) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać wszsystkie obiekty klasy {@link ReservationEntity} dla klienta z podanym loginem
     *
     * @param login       login klienta, dla którego chcemy pobrac rezerwacje
     * @param getCanceled flaga określająca czy lista ma zwierać anulowane rezerwacjie
     * @param getPast     flaga określająca czy lsita ma zwierać rezerwacje, których data zakończenia jest w przeszłości
     * @return lista rezerwacji dla danego klienta
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> findByCustomer(String login, boolean getCanceled, boolean getPast) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać wszsystkie obiekty klasy {@link ReservationEntity}
     *
     * @param getCanceled flaga określająca czy lista ma zwierać anulowane rezerwacjie
     * @param getPast     flaga określająca czy lsita ma zwierać rezerwacje, których data zakończenia jest w przeszłości
     * @return lista rezerwacji
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> findAll(boolean getCanceled, boolean getPast) throws AppException;
    
    /**
     * Implementacja tej metody powinna zwracać wszystkie obiekty klasy {@link ReservationEntity} konfliktujące z podanymi parametrami nowej rezerwacji.
     *
     * @param startDate          początek nowej rezerwacji.
     * @param endDate            koniec nowej rezerwacji.
     * @param weaponModelName    nazwa modelu broni.
     * @param alleyName          nazwa toru.
     * @return lista konfliktujących rezerwacji.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> findConflictReservationsByWeaponModel(LocalDateTime startDate, LocalDateTime endDate,
                                                                  String weaponModelName, String alleyName) throws AppException;


    /**
     * Implementacja tej metody powinna zwracać wszystkie obiekty klasy {@link ReservationEntity} konfliktujące z podanymi parametrami nowej rezerwacji.
     *
     * @param startDate          początek nowej rezerwacji.
     * @param endDate            koniec nowej rezerwacji.
     * @param weaponSerialNumber numer seryjny broni.
     * @param alleyName          nazwa toru.
     * @return lista konfliktujących rezerwacji.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> findConflictReservationsByWeapon(LocalDateTime startDate, LocalDateTime endDate,
                                                             String weaponSerialNumber, String alleyName) throws AppException;
}
