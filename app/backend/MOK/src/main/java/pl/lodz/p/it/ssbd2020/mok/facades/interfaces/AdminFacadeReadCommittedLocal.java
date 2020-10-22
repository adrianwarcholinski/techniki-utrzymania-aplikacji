package pl.lodz.p.it.ssbd2020.mok.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AdminEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje dla klasy encyjnej {@link AdminEntity}.
 * Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface AdminFacadeReadCommittedLocal extends BasicFacadeOperations<AdminEntity> {

    /**
     * Implementacja tej metody powinna zwracać ilość aktywnych albo nieaktywnych kont administratów.
     *
     * @param active Określa czy wyszukiwane konta mają być aktywne (true - aktywne, false - nieaktywne).
     * @return Liczba aktywnych albo nieaktywnych kont adminstratora.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Long countActive(boolean active) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy encyjnej {@link AdminEntity} o podanym numerze karty.
     *
     * @param cardNumber numer karty przypisany do administratora
     * @return administratora o podanym numerze karty lub
     * pusty Optional, jeżeli taki administrator nie istnieje
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<AdminEntity> findByCardNumber(String cardNumber) throws AppException;

}
