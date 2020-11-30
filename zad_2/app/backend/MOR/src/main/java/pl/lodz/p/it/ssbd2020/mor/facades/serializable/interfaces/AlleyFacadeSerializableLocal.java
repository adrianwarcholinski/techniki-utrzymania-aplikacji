package pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
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
public interface AlleyFacadeSerializableLocal extends BasicFacadeOperations<AlleyEntity> {
    /**
     * Implementacja tej metody powinna zwrócić obiekt klasy {@link AlleyEntity} zgodnie z przekazanym parametrem.
     *
     * @param name nazwa.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<AlleyEntity> findByName(String name) throws AppException;
}
