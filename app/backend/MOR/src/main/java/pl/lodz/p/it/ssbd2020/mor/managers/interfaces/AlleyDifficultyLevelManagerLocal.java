package pl.lodz.p.it.ssbd2020.mor.managers.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AlleyDifficultyLevelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;

import javax.ejb.Local;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez menadżerów umożliwiający operacje na poziomach trudności torów.
 */
@Local
public interface AlleyDifficultyLevelManagerLocal extends Manager {

    /**
     * Implementacja tej metody powinna zwracać wszystkie poziomy trudności toru.
     *
     * @return wszystkie znalezione poziomy trudności toru
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AlleyDifficultyLevelEntity> getAllAlleyDifficultyLevels() throws AppException;

    /**
     * Implementacja tej metody powinna zwracać poziom trudności toru o zadanej nazwie.
     *
     * @param name poszukiwany poziom trudności.
     * @return poziom trudności toru o podanej nazwie.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    AlleyDifficultyLevelEntity findByName(String name) throws AppException;
}
