package pl.lodz.p.it.ssbd2020.mok.managers.interfaces;

import pl.lodz.p.it.ssbd2020.entities.AccountEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.utils.LastAuthenticationTimes;
import pl.lodz.p.it.ssbd2020.utils.manager.Manager;
import pl.lodz.p.it.ssbd2020.entities.*;

import javax.ejb.Local;
import java.util.List;

/**
 * Interfejs przeznaczony do zaimplementowania przez menadżerów umożliwiając operacje na danych użytkowników.
 */
@Local
public interface AccountManagerLocal extends Manager {


    /**
     * Implementacja tej metody powinna dodawać konto nowego użytkownika.
     *
     * @param accountEntity encja reprezentująca konto, które dodajemy
     * @param language      język używany w wiadomości e-mail
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void addAccount(AccountEntity accountEntity, String language) throws AppException;

    /**
     * Implementacja tej metody powinna weryfikować konto użytkownika.
     *
     * @param login    login konta.
     * @param language język używany w wiadomośći e-mail.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void verify(String login, String language) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać konto użytkownika o podanym loginie.
     *
     * @param login login przypisany do konta
     * @return znalezione konto
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    AccountEntity getAccountDetails(String login) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać wszystkie konta.
     *
     * @return wszystkie konta
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AccountEntity> getAllAccounts() throws AppException;

    /**
     * Implementacja tej metody powinna modyfikować dane użytkownika.
     *
     * @param accountEntity  encja reprezentująca dane, które edytujemy.
     * @param adminEntity    encja reprezentująca numer karty administratora.
     * @param customerEntity encja reprezentująca numer telefeonu klienta.
     * @param employeeEntity encja reprezentująca numer służbowy telefeonu pracownika.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void editUserDetails(AccountEntity accountEntity, AdminEntity adminEntity, CustomerEntity customerEntity,
                         EmployeeEntity employeeEntity) throws AppException;

    /**
     * Implementacja tej metody powinna zmieniać hasło użytkownika pod warunkiem, że stare hasło pokrywa się ze stanem utrwalonym w bazie danych.
     *
     * @param login       login użytkownika, u którego zmieniane jest hasło
     * @param oldPassword dotychczasowe hasło użytkownika, które musi pokrywać się ze stanem utrwalonym w bazie danych.
     * @param newPassword nowe hasło, które musi zawierać przynajmniej jedną wielką literę, przynajmniej jedną małą literę,
     *                    przynajmniej jeden znak specjalny. Ponadto musi zawierać co najmniej 8 znaków.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void changeOwnPassword(String login, String oldPassword, String newPassword) throws AppException;

    /**
     * Implementacja tej metody powinna zmieniać hasło użytkownika.
     *
     * @param login       login użytkownika, którego hasło chcemy zmienić
     * @param newPassword nowe hasło, które musi zawierać przynajmniej jedną wielką literę, przynajmniej jedną małą literę,
     *                    przynajmniej jeden znak specjalny. Ponadto musi zawierać co najmniej 8 znaków
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void changePassword(String login, String newPassword) throws AppException;


    /**
     * Implementacja tej metody powinna dodawać poziom dostęu do konta.
     *
     * @param accessLevel dodawany poziom dostępu.
     * @param login       login użytkownika.
     * @param info        jeśli poziom dostępu to klient wartość ta to numer telefonu
     *                    jeśli poziom dostępu to pracownik wartość ta to firmowy numer telefonu
     *                    jeśli poziom dostępu to administrator wartość ta to numer karty
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void grantAccessLevel(String accessLevel, String login, String info) throws AppException;

    /**
     * Implementacja tej metody powinna usuwać poziom dostęu do konta.
     *
     * @param accessLevel odbierany poziom dostępu.
     * @param login       login użytkownika.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void revokeAccessLevel(String login, String accessLevel) throws AppException;

    /**
     * Implementacja tej metody powinna blokować konto użytkownika.
     *
     * @param login login użytkownika
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void lockAccount(String login) throws AppException;

    /**
     * Implementacja tej metody powinna odblokowywać konto użytkownika.
     *
     * @param login login użytkownika
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void unlockAccount(String login) throws AppException;

    /**
     * Implementacja tej metody powinna weyfikować czy w naszej aplikacji jest zweryfikowany użytkownik z podanym adresem email.
     *
     * @param email email użytkownika
     * @return {@code true} - jeśli istnieje zweryfikowane konto o podanym adresie email
     * {@code false} - jeśli nie istnieje konto o podanym adresie lub jest niezweryfikowane
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    boolean isVerifiedAccountWithEmail(String email) throws AppException;

    /**
     * Implementacja tej metody powinna wysyłać wiadomość e-mail zawierającą link do zmiany hasła użytkownika.
     *
     * @param email           adres e-mail, na który będzie wysłana wiadomość z linkiem (pod warunkiem, że taki e-mail jest
     *                        przypisany do jakiegoś konta)
     * @param browserLanguage język używany na potrzeby internacjonalizacji linku
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void sendEmailForResetPassword(String email, String browserLanguage) throws AppException;

    /**
     * Implementacja tej metody powinna resetować hasło użytkownikowi.
     *
     * @param login       login użytkownika, u którego resetowane jest hasło
     * @param newPassword nowe hasło, które musi zawierać przynajmniej jedną wielką literę, przynajmniej jedną małą literę,
     *                    przynajmniej jeden znak specjalny. Ponadto musi zawierać co najmniej 8 znaków.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void resetPassword(String login, String newPassword) throws AppException;

    /**
     * Implementacja tej metody powinna aktualizować czas ostatniej niepomyślnej próby uwierzytelnienia użytkownika i liczbę kolejnych niepomyślnych prób.
     *
     * @param login login użytkownika, który próbował się uwierzytelnić
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void handleUnsuccessfulAuthenticationAttempt(String login) throws AppException;

    /**
     * Implementacja tej metody powinna blokować konto jeśli, kolejno po sobie wystąpiły trzy niepomyślne próby uwierzytelnienia
     * Wysyła email o zablokowaniu konta
     *
     * @param login    login użytkownika, który próbował się uwierzytelnić
     * @param language język używany w wiadomośći e-mail.
     * @return zwraca wartość logiczną true jeśli konto zostało zablokowane albo false jeśli nie zostanie zablokowane
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    boolean lockAccountAfterFailedAuthentication(String login, String language) throws AppException;

    /**
     * Implementacja tej metody powinna aktualizować czas ostatniej pomyślnej próby uwierzytelnienia użytkownika oraz aktualizuje jego ostatnio używany adres IP.
     * Wyzerowuje licznik występujących kolejno po sobie, niepoprawnych prób uwierzytelnienia.
     *
     * @param login   login użytkownika, który się uwierzytelnił
     * @param ip      adres ip z którego użytkownik się uwierzytelnił
     * @param lang    język używany w wiadomośći e-mail.
     * @param isAdmin pozwal stwierdzić czy uwierzytelniający jest administratorem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void handleSuccessfulAuthentication(String login, String ip, String lang, boolean isAdmin) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać datę i godzinę ostatniego pomyślnego uwierzytelnienia i datę ostatniego niepomyślnego uwierzytelnienia użytkownika.
     *
     * @param login login użytkownika
     * @return LastAuthenticationTimes obiekt zawierający czas ostatniego pomyślnego i ostatniego niepomyślnego uwierzytelnienia,
     * o ile konto użytkownika istnieje.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    LastAuthenticationTimes getLastAuthenticationTimes(String login) throws AppException;


    /**
     * Implementacja tej metody powinna zwracać listę obiektów klasy {@link AccountEntity}
     * w których pola odpowiadające za imię i nazwisko zawierają w sobie podaną frazę
     *
     * @param phrase Fraza, która może występować w imieniu i nazwisku przypisanym do konta.
     * @return Lista kont zgodna z podanym argumentem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AccountEntity> findByPhraseInFullName(String phrase) throws AppException;

    /**
     * Implementacja tej metody powinna wysyłac wiadomość e-mail zawierającą link do potwierdzenia zmiany e-maila użytkownika.
     *
     * @param login    login użytkownika, którego email chcemy zmienić.
     * @param newEmail adres e-mail, na który będzie wysłana wiadomość z linkiem (pod warunkiem, że taki e-mail jest
     *                 przypisany do jakiegoś konta).
     * @param lang     język używany na potrzeby internacjonalizacji wiadomości email.
     * @param byAdmin  flaga określająca czy zmiany adresu e-mail dokonał właściciel konta, czy administrator
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void sendEmailForChangeEmail(String login, String newEmail, String lang, boolean byAdmin) throws AppException;

    /**
     * Implementacja tej metody powinna zmieniac adres e-mail konta na podstawie przekazanego szyfrogramu.
     *
     * @param cipherText szyfrogram zawierający login użytkownika i adres email
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void changeEmail(String cipherText) throws AppException;

    /**
     * Implementacja tej metody powinna zwracać konta, które pomyślnie przeszły proces uwierzytelnienia.
     *
     * @return Konta, które pomyślnie przeszły proces uwierzytelnienia.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    List<AccountEntity> getAuthenticatedAccounts() throws AppException;

    /**
     * Implementacja tej metody powinna wysyłaćlink weryfikacyjny do użytkownika o podanym loginie.
     *
     * @param login    Login użytkownika do którego ma zostać wysłany link weryfikacyjkny.
     * @param language Język treści wiadomości.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    void sendVerificationLink(String login, String language) throws AppException;
}
