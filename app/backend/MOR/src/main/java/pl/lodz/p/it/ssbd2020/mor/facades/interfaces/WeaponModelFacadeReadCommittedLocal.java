package pl.lodz.p.it.ssbd2020.mor.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link WeaponModelEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface WeaponModelFacadeReadCommittedLocal extends BasicFacadeOperations<WeaponModelEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy {@link WeaponModelEntity} zgodnie z przekazanym parametrem.
     *
     * @param name nazwa modelu broni.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<WeaponModelEntity> findByName(String name) throws AppException;


    /**
     * Implementacja tej metody powinna zwracać obiekty klasy {@link WeaponModelEntity} zgodnie z przekazanym parametrem.
     *
     * @param active flaga, której wartość określa aktywność broni
     *               (true - aktywna, false - nieaktywna)
     * @return listę aktywnych albo nieaktywnych obiektów klasy {@link WeaponModelEntity}.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<WeaponModelEntity> findByActive(boolean active) throws AppException;
}
