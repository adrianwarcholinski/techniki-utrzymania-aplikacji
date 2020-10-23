package pl.lodz.p.it.ssbd2020.mor.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje
 * dla klasy encyjnej {@link AlleyEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface AlleyFacadeReadCommittedLocal extends BasicFacadeOperations<AlleyEntity> {
    /**
     * Implementacja tej metody powinna zwracać obiekt klasy {@link AlleyEntity} zgodnie z przekazanym parametrem.
     *
     * @param name nazwa.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<AlleyEntity> findByName(String name) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać obiekty klasy {@link AlleyEntity} zgodnie z przekazanym parametrem.
     *
     * @param active flaga, której wartość określa aktywność broni
     *               (true - aktywna, false - nieaktywna)
     * @return listę aktywnych albo nieaktywnych torów.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AlleyEntity> findByActive(boolean active) throws AppException;
}
