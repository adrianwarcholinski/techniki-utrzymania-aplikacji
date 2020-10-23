package pl.lodz.p.it.ssbd2020.mor.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link WeaponEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface WeaponFacadeReadCommittedLocal extends BasicFacadeOperations<WeaponEntity> {
    /**
     * Implementacja tej metody powinna zwracać obiekty klasy {@link WeaponEntity} zgodnie z przekazanym parametrem.
     *
     * @param active flaga, której wartość określa aktywność broni
     *               (true - aktywna, false - nieaktywna)
     * @return listę aktywnych albo nieaktywnych obiektów klasy {@link WeaponEntity}.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<WeaponEntity> findByActive(boolean active) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać obiekty klasy {@link WeaponEntity} zgodnie z przekazanym parametrem.
     *
     * @param active          flaga, której wartość określa aktywność broni
     *                        (true - aktywna, false - nieaktywna)
     * @param weaponModelName nazwa modelu broni
     * @return listę aktywnych albo nieaktywnych obiektów klasy {@link WeaponEntity} danego modelu broni.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<WeaponEntity> findByActiveAndWeaponModelName(boolean active, String weaponModelName) throws AppException;
}
