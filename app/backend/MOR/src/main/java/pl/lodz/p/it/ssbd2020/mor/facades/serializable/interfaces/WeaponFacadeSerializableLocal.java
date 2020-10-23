package pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces;

import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link WeaponEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface WeaponFacadeSerializableLocal extends BasicFacadeOperations<WeaponEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy {@link WeaponEntity} zgodnie z przekazanym parametrem.
     *
     * @param serialNumber numer seryjny broni.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<WeaponEntity> findBySerialNumber(String serialNumber) throws AppException;
}
