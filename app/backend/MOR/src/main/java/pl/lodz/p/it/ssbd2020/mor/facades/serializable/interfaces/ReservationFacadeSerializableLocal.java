package pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces;

import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link ReservationEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface ReservationFacadeSerializableLocal extends BasicFacadeOperations<ReservationEntity> {
    
    /**
     * Implementacja tej metody powinna zwracać wszystkie obiekt klasy {@link ReservationEntity} konfliktujące
     * z podanymi parametrami nowej rezerwacji.
     *
     * @param startDate          początek nowej rezerwacji.
     * @param endDate            koniec nowej rezerwacji.
     * @param weaponModelName    nazwa modelu broni.
     * @param alleyName          nazwa toru.
     * @return lista konfliktujących rezerwacji.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> findConflictReservationsByWeaponModel(LocalDateTime startDate,
                                                                  LocalDateTime endDate,
                                                                  String weaponModelName,
                                                                  String alleyName) throws AppException;


    /**
     * Implementacja tej metody powinna zwracać wszystkie obiekty klasy {@link ReservationEntity}
     * rezerwacje konfliktujące z podanymi parametrami nowej rezerwacji.
     *
     * @param startDate          początek nowej rezerwacji.
     * @param endDate            koniec nowej rezerwacji.
     * @param weaponSerialNumber numer seryjny broni.
     * @param alleyName          nazwa toru.
     * @return lista konfliktujących rezerwacji.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<ReservationEntity> findConflictReservationsByWeapon(LocalDateTime startDate,
                                                             LocalDateTime endDate,
                                                             String weaponSerialNumber,
                                                             String alleyName) throws AppException;
}
