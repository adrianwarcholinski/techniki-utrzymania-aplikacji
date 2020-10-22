package pl.lodz.p.it.ssbd2020.mok.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AccessLevelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje dla klasy encyjnej {@link AccessLevelEntity}.
 * Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface AccessLevelFacadeReadCommittedLocal extends BasicFacadeOperations<AccessLevelEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy encyjnej {@link AccessLevelEntity}
     * określonego typu dla użytkownika o podanym loginie.
     *
     * @param accessLevel Nazwa poziomu dostępu.
     * @param login       Login użytkownika.
     * @return poziom dostępu lub pusty optional jeśli użytkownik z danym loginie nie posiada poziomu dostępu.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<AccessLevelEntity> findByAccessLevelAndLogin(String accessLevel, String login) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać liczbę poziomów dostępu użytkownika
     *
     * @param login Login użytkownika
     * @return liczba roli jaką posiada użytkownik lub pusty optional.
     * @throws AppException jeślioperacja zakończy się niepowodzeniem.
     */
    Optional<Long> countAccountRoles(String login) throws AppException;
}
