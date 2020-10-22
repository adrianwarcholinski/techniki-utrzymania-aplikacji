package pl.lodz.p.it.ssbd2020.mor.managers.interfaces;

import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;

import javax.ejb.Local;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez menadżerów umożliwiający operacje na rezerwacjach.
 */
@Local
public interface ReservationManagerLocal extends Manager {

    /**
     * Implementacja tej metody powinna zwracać wszystkie rezerwacje.
     *
     * @param getCanceled flaga określająca czy lista ma zwierać anulowane rezerwacjie
     * @param getPast     flaga określająca czy lsita ma zwierać rezerwacje, których data zakończenia jest w przeszłości
     * @return lista wszystkich rezerwacji
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> getAllReservations(boolean getCanceled, boolean getPast) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać wszystkie rezerwacje aktualnie zalogowanego użytkownika.
     *
     * @param login       login klienta, dla którego chcemy pobrac rezerwacje
     * @param getCanceled flaga określająca czy lista ma zwierać anulowane rezerwacjie
     * @param getPast     flaga określająca czy lsita ma zwierać rezerwacje, których data zakończenia jest w przeszłości
     * @return lista rezerwacji danego klienta
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> getAllCustomersReservations(String login, boolean getCanceled, boolean getPast) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać dane rezerwacji.
     *
     * @param reservationNumber numer rezerwacji, której dane chcemy pobrać.
     * @return znaleziona rezerwacja
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    ReservationEntity getReservation(Long reservationNumber) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać dane rezerwacji danego użytkownika.
     *
     * @param login             login użytkownika, którego rezerwację chcemy pobrać.
     * @param reservationNumber numer rezerwacji, której dane chcemy pobrać.
     * @return znaleziona rezerwacja
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    ReservationEntity getOwnReservation(String login, Long reservationNumber) throws AppException;

    /**
     * Implementacja tej metody powinna modyfikować dane rezerwacji podanego użytkownika.
     *
     * @param login    login użytkownika, którego modyfikacji rezerwacji chcemy dokonać.
     * @param entity   encja {@link ReservationEntity} reprezentująca dane rezerwacji, które edytujemy.
     * @param language Język w jakim zostanie wysłąna wiadomośc email do klienta z informacją o edycji
     *                 rezerwacji.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void updateOwnReservation(String login, ReservationEntity entity, String language) throws AppException;

    /**
     * Implementacja tej metody powinna modyfikować dane rezerwacji.
     *
     * @param entity   encja reprezentująca dane rezerwacji, które edytujemy.
     * @param language język w jakim zostanie wysłąna wiadomośc email do klienta z informacją o edycji
     *                 rezerwacji.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void updateReservation(ReservationEntity entity, String language) throws AppException;

    /**
     * Implementacja tej metody powinna dodawać nową rezerwacje.
     *
     * @param entity          rezerwacja do utworzenia.
     * @param alleyName       nazwa toru do rezerwazji.
     * @param weaponModelName nazwa modelu broni do rezerwacji.
     * @param language        język w jakim zostanie wysłąna wiadomośc email do klienta z informacją o dokananiu rezerwacj.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void makeReservation(ReservationEntity entity, String alleyName, String weaponModelName, String language) throws AppException;

    /**
     * Implementacja tej metody powinna odwoływać rezerwację.
     *
     * @param reservationNumber numer odwoływanej rezerwacja
     * @param language          język używany w wiadomości e-mail
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void cancelReservation(long reservationNumber, String language) throws AppException;


    /**
     * Implementacja tej metody powinna odwoływać rezerwację.
     *
     * @param reservationNumber numer odwoływanej rezerwacja
     * @param login             login użytkownika podejmującego próbę odwołania
     * @param language          język używany w wiadomości e-mail
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void cancelReservation(long reservationNumber, String login, String language) throws AppException;


    /**
     * Implementacja tej metody powinna zwracać konfliktowe rezerwację dla podanego toru, nazwy modelu broni oraz daty.
     *
     * @param date                      data dla której mają zostać zwrócone rezerwacje.
     * @param alleyName                 nazwa toru dla któego mają zostać zwrócone rezerwacji.
     * @param weaponModelName           nazwa modelu borni dla której mają zostać zwrócone rezerwacji.
     * @param excludedReservationNumber numer rezerwacji, która ma zostać wykluczona z listy konfliktowych rezerwacji.
     * @return lista rezerwacji dla podanego toru, daty oraz numeru seryjnego broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> getConflictReservationsByWeaponModel(LocalDateTime date,
                                                                 String alleyName,
                                                                 String weaponModelName,
                                                                 Long excludedReservationNumber) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać konfliktowe rezerwację dla podanego toru, numeru seryjnego broni oraz daty.
     *
     * @param date                      data dla której mają zostać zwrócone rezerwacje.
     * @param alleyName                 nazwa toru dla któego mają zostać zwrócone rezerwacji.
     * @param weaponSerialNumber        numer seryjny broni dla któej mają zostać zwrócone rezerwacje.
     * @param excludedReservationNumber numer rezerwacji, która ma zostać wykluczona z listy konfliktowych rezerwacji.
     * @return lista rezerwacji dla podanego toru, daty oraz numeru seryjnego broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> getConflictReservationsByWeapon(LocalDateTime date,
                                                            String alleyName,
                                                            String weaponSerialNumber,
                                                            long excludedReservationNumber) throws AppException;
}
