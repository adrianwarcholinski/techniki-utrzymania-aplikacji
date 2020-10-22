package pl.lodz.p.it.ssbd2020.mor.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link WeaponCategoryEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface WeaponCategoryFacadeReadCommittedLocal extends BasicFacadeOperations<WeaponCategoryEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy {@link WeaponCategoryEntity} zgodnie z przekazanym parametrem.
     *
     * @param name nazwa kategorii broni.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<WeaponCategoryEntity> findByName(String name) throws AppException;
}
