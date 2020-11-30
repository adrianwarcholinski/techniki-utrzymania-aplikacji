package pl.lodz.p.it.ssbd2020.mor.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.OpinionEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link OpinionEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface OpinionFacadeReadCommittedLocal extends BasicFacadeOperations<OpinionEntity> {

    /**
     * Implementacja tej metody powinna wyszukiwać i zwracać obiekt klasy {@link OpinionEntity} na podstawie jej numeru.
     *
     * @param opinionNumber numer poszukiwanej opinii.
     * @return znaleziona opinia lub pusty optional, jeżeli opinia o podanym numerze nie zostanie znaleziona.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<OpinionEntity> findByOpinionNumber(long opinionNumber) throws AppException;

    /**
     * Implementacja tej metody powinna wyszukiwać i zwracać obiekt klasy {@link OpinionEntity} dodaną przez określonego użytkownika na temat
     * określonego modelu broni.
     *
     * @param weaponModelName nazwa modelu broni
     * @param login           login użytkownika
     * @return opinia użytkownika na temat modelu broni lub {@code null},
     * jeżeli użytkownik nie ocenił tego modelu broni
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<OpinionEntity> findByWeaponModelAndCustomerLogin(String weaponModelName, String login) throws AppException;
}
