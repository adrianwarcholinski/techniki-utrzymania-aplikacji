package pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces;

import pl.lodz.p.it.ssbd2020.entities.CustomerEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.facades.BasicFacadeOperations;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs przeznaczony do zaimplementowania przez fasady udostępniające podstawowe oraz dodatkowe
 * operacje dla klasy encyjnej {@link CustomerEntity}. Rozszerza interfejs {@link BasicFacadeOperations}
 *
 * @see BasicFacadeOperations
 */
@Local
public interface CustomerFacadeSerializableLocal extends BasicFacadeOperations<CustomerEntity> {
    /**
     * Implementacja tej metody powinna zwracać obiekt klasy {@link CustomerEntity} zgodnie z podanym loginem.
     *
     * @param login login przypisany do klienta
     * @return klient o podanym loginie lub pusty Optional, jeżeli taki klient nie istnieje.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<CustomerEntity> findByLogin(String login) throws AppException;
}
