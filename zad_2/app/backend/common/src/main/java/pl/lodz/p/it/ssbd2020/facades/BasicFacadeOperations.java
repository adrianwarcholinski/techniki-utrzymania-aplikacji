package pl.lodz.p.it.ssbd2020.facades;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;


/**
 * Interfejs przeznaczony do zaimplementowania przez fasady, które udostępniają podstawowe operacje.
 *
 * @param <T> typ encji, której obiekty są przetwarzane przez fasadę.
 */
@Local
public interface BasicFacadeOperations<T> {

    /**
     * Implementacja tej metody powinna dołączyć encję do kontekstu trwałości i wymusić utrwalenie w bazie danych.
     *
     * @param obj encja dołączana do kontekstu trwałości
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void create(T obj) throws AppException;

    /**
     * Implementacja tej metody powinna zaktualizować encję znajdującą się w kontekście trwałości i wymusić utrwalenie
     * zmian w bazie danych.
     *
     * @param obj zaktualizowana encja
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void edit(T obj) throws AppException;

    /**
     * Implementacja tej metody powinna usunąć encję z kontekstu trwałości i wymusić usunięcie jej z bazy danych.
     *
     * @param obj usuwana encja
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void remove(T obj) throws AppException;

    /**
     * Implementacja tej metody powinna zwrócić obiekt encji reprezentujący krotkę
     * o podanym identyfikatorze.
     *
     * @param id identyfikator poszukiwanego obiektu
     * @return liczba obiektów encji
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    Optional<T> find(Object id) throws AppException;

    /**
     * Implementacja tej metody powinna zwrócić ilość utrwalonych obiektów encji.
     *
     * @return ilośc utwalonych obiektów.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    int count() throws AppException;

    /**
     * Implementacja tej metody powinna zwrócić listę wszystkie obiekty encji znajdujące się w kontekście trwałości.
     *
     * @return lista obiektów encji
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<T> findAll() throws AppException;
}
