package pl.lodz.p.it.ssbd2020.mor.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AlleyDifficultyLevelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link AlleyDifficultyLevelEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface AlleyDifficultyLevelFacadeReadCommittedLocal extends BasicFacadeOperations<AlleyDifficultyLevelEntity> {
    /**
     * Implementacja tej metody powinna zwracać obiekt klasy {@link AlleyDifficultyLevelEntity} zgodnie z przekazanym parametrem.
     *
     * @param name nazwa.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<AlleyDifficultyLevelEntity> findByName(String name) throws AppException;
}
