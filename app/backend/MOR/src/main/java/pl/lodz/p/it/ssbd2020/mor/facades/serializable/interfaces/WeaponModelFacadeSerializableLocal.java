package pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces;

import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje dla klasy encyjnej {@link WeaponModelEntity}.
 * Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface WeaponModelFacadeSerializableLocal extends BasicFacadeOperations<WeaponModelEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy {@link WeaponModelEntity} zgodnie z przekazanym parametrem.
     *
     * @param name nazwa modelu broni.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<WeaponModelEntity> findByName(String name) throws AppException;
}
