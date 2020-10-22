package pl.lodz.p.it.ssbd2020.mok.facades.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AccountEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe operacje dla klasy encyjnej {@link AccountEntity}.
 * Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface AccountFacadeReadCommittedLocal extends BasicFacadeOperations<AccountEntity> {

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy encyjnej {@link AccountEntity} o podanym loginie
     *
     * @param login login przypisany do konta
     * @return konto o podanym loginie lub
     * pusty Optional, jeżeli takie konto nie istnieje
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<AccountEntity> findByLogin(String login) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać obiekt klasy encyjnej {@link AccountEntity} o podanym adresie e-mail
     *
     * @param email adres e-mail przypisany do konta
     * @return konto o podanym adresie e-mail lub
     * pusty Optional, jeżeli takie konto nie istnieje
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<AccountEntity> findByEmail(String email) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać obiekty klasy encyjnej {@link AccountEntity},
     * które zawierają w konkatenacji imienia i nazwiska podaną frazę
     *
     * @param phrase fraza, którą wyszukujemy w konkatenacji imienia i nazwiska
     * @return konta zawierające w konkatenacji imienia i nazwiska podaną frazę
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AccountEntity> findByPhraseInFullName(String phrase) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać obiekty klasy encyjnej {@link AccountEntity},
     * które pomyślnie przeszły proces uwierzytelnienia.
     *
     * @return konta, które pomyślnie przeszły proces uwierzytelnienia.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AccountEntity> findAuthenticated() throws AppException;

    /**
     * Implementacja tej metody powinna zwracać obiekty klasy encyjnej {@link AccountEntity}
     * kont posortowaną po id.
     *
     * @return konta zawierające w konkatenacji imienia i nazwiska podaną frazę
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AccountEntity> getAllSortedById() throws AppException;

}
