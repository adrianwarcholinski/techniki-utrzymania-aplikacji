package pl.lodz.p.it.ssbd2020.mok.endpoints;

import pl.lodz.p.it.ssbd2020.entities.*;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InternalProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.*;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.mok.endpoints.dto.*;
import pl.lodz.p.it.ssbd2020.mok.managers.interfaces.AccountManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.Crypt;
import pl.lodz.p.it.ssbd2020.utils.LinkUtils;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;
import pl.lodz.p.it.ssbd2020.utils.captcha.CaptchaUtils;
import pl.lodz.p.it.ssbd2020.utils.endpoint.Endpoint;
import pl.lodz.p.it.ssbd2020.utils.interceptor.MethodInvocationInterceptor;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa zawierająca definicje punktów końcowych dotyczących kont użytkowników.
 */
@Path("account")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
public class AccountEndpoint extends Endpoint {

    /**
     * Kontekst bezpieczeństwa, który zawiera informacje o tożsamości użytkownika.
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Kontekst serwletu, który pozwala odczytywać parametry z deskryptora web.xml.
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z kontem.
     */
    @Inject
    private AccountManagerLocal accountManager;

    /**
     * Ziarno, które pozwala walidować token captcha.
     */
    @Inject
    private CaptchaUtils captchaUtils;

    /**
     * Ziarno, które pozwala na szyfrowanie i deszyfrowanie łańcuchów znaków
     */
    @Inject
    private Crypt crypt;

    /**
     * Ziarno, które udostępnia metody pomocnicze dla tokenów
     */
    @Inject
    private LinkUtils linkUtils;

    /**
     * Metoda pobierająca z deskryptora web.xml maksymalną liczbę prób wykonania transakcji.
     */
    @PostConstruct
    public void init() {
        maxTransactions = Integer.parseInt(servletContext.getInitParameter("RENEW_TRANSACTION_LIMIT"));
    }

    /**
     * Gość samodzielnie tworzy konto nowego użytkownika -
     * takie konto ma przydzielony poziom dostępu ROLE_CUSTOMER.
     *
     * @param account      obiekt klasy {@link AccountDto} transferujący dane nowo powstałego konta
     * @param language     język używany na potrzeby wysyłania wiadomości e-mail
     * @param captchaToken token pozyskany z mechanizmu reCAPTCHA
     * @return odpowiedź z kodem 200 w przypadku pomyślnej rejestracji użytkownika,
     * odpowiedź z kodem 400 w przypadku niepowodzenia operacji
     */
    @POST
    @Path("register")
    @PermitAll
    public Response register(@Valid AccountDto account,
                             @HeaderParam("language") String language,
                             @HeaderParam("captchaToken") String captchaToken) {
        if (language == null || language.isBlank()) {
            language = "en";
        }
        try {
            validateCaptchaToken(captchaToken);
            AccountEntity accountEntity = AccountDto.convertToAccountEntity(account);
            accountEntity.setActive(true);
            CustomerEntity customerEntity = (CustomerEntity) AccountDto.createAccountAccessLevelEntity("ROLE_CUSTOMER", accountEntity, account);
            customerEntity.setActive(true);
            accountEntity.getAccessLevels().add(customerEntity);

            String finalLanguage = language;
            performTransaction(accountManager, () -> accountManager.addAccount(accountEntity, finalLanguage));

            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator tworzy konto innego użytkownika podając wszystkie jego dane i
     * wybrany dla niego poziom dostępu.
     *
     * @param accountDto   obiekt klasy {@link AccountDto} transferujący dane nowo powstałego konta
     * @param accessLevel  poziom dostępu nowego konta
     * @param language     język wiadomości email
     * @param captchaToken token pozyskany z mechanizmu reCAPTCHA
     * @return odpowiedź z kodem 200 w przypadku pomyślnego utworzenia i dodania nowego użytkownika,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (login lub email jest już zajęty)
     */
    @POST
    @Path("add")
    @RolesAllowed("addAccount")
    public Response addAccount(@Valid AccountDto accountDto,
                               @HeaderParam("accessLevel") @NotBlank String accessLevel,
                               @HeaderParam("language") String language,
                               @HeaderParam("captchaToken") String captchaToken) {
        if (language == null || language.isBlank()) {
            language = "en";
        }
        try {
            validateCaptchaToken(captchaToken);
            accountDto.check(accessLevel);
            AccountEntity accountEntity = AccountDto.convertToAccountEntity(accountDto);
            accountEntity.setActive(true);
            AccessLevelEntity accessLevelEntity = AccountDto.createAccountAccessLevelEntity(accessLevel, accountEntity, accountDto);
            accessLevelEntity.setActive(true);
            accountEntity.getAccessLevels().add(accessLevelEntity);

            String finalLanguage = language;
            performTransaction(accountManager, () -> accountManager.addAccount(accountEntity, finalLanguage));

            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }


    /**
     * Administrator edytuje szczegóły konta innego użytkownika.
     *
     * @param editAccountDto obiekt klasy {@link EditAccountDto} z zedytowanymi danymi użytkownika.
     * @return odpowiedź z kodem 200 w przypadku pomyślnie przeprowadzonej edycji danych użytkownika,
     * odpowiedź z kodem 400 w przypadku Accounniepowodzenia (konto nie istniej, podane dane były błedne lub
     * nie powiodła się operacja odszyfrowania danych wersji i identyfikatora)
     */
    @PUT
    @Path("edit")
    @RolesAllowed("editAccount")
    public Response editAccount(@Valid EditAccountDto editAccountDto) {
        try {
            this.performEditAccount(editAccountDto);
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }


    /**
     * Użytkownik edytuje szczegóły swojego konta.
     *
     * @param editAccountDto obiekt klasy {@link EditAccountDto} z zedytowanymi danymi użytkownika.
     * @param captchaToken token pozyskany z mechanizmu reCAPTCHA
     * @return odpowiedź z kodem 200 w przypadku pomyślnie przeprowadzonej edycji danych użytkownika,
     * odpowiedź z kodem 400 w przypadku Accounniepowodzenia (konto nie istniej, podane dane były błedne lub
     * nie powiodła się operacja odszyfrowania danych wersji i identyfikatora)
     */
    @PUT
    @Path("edit-own")
    @RolesAllowed("editOwnAccount")
    public Response editOwnAccount(@Valid EditAccountDto editAccountDto,
                                   @HeaderParam("captchaToken") String captchaToken) {
        try {
            validateCaptchaToken(captchaToken);
            String login = securityContext.getCallerPrincipal().getName();
            editAccountDto.setLogin(login);

            this.performEditAccount(editAccountDto);
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Użytkownik używa linku wysłanego w wiadomości email, potwierdzając tym samym weryfikację konta.
     *
     * @param toVerify zaszyfrowany tekst zawierający datę wysłania linka oraz login użytkownika
     * @param language język używany na potrzeby wysyłania wiadomości e-mail
     * @return odpowiedź z kodem 200 w przypadku pomyślnej weryfikacji nowego użytkownika,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (link już użyty lub spreparowany)
     */
    @PUT
    @Path("verify")
    @PermitAll
    public Response verifyAccount(@QueryParam("toVerify") String toVerify,
                                  @QueryParam("language") String language) {
        if (language == null || language.isBlank()) {
            language = "en";
        }
        try {
            String[] decrypted = crypt.decrypt(toVerify).split(";");

            if ((decrypted.length != 2) || LocalDateTime.now().isBefore(LocalDateTime.parse(decrypted[0]))) {
                throw new LinkCorruptedException();
            }

            String login = decrypted[1];

            String finalLanguage = language;
            performTransaction(accountManager, () -> accountManager.verify(login, finalLanguage));

            return Response.ok().build();
        } catch (DecryptException e) {
            LinkCorruptedException exception = new LinkCorruptedException();
            return exception.getResponse();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator dołącza kolejny poziom dostępu do już istniejącego konta,
     * które danego poziomu dostępu jeszcze nie ma.
     *
     * @param login      login użytkownika, któremu nadajemy poziom dostępu.
     * @param cardNumber numer karty przypisany do poziomu dostępu administrator.
     * @return odpowiedź z kodem 200 w przypadku pomyślnego dodania nowego poziomu dostępu,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (poziom dostępu jest już przyznany, konto nie istnieje)
     */
    @PUT
    @Path("add-admin-access-level")
    @RolesAllowed("grantAdminAccessLevel")
    public Response grantAdminAccessLevel(@HeaderParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login,
                                          @HeaderParam("param") @NotBlank @Pattern(regexp = RegexPatterns.CARD_NUMBER) String cardNumber) {
        try {
            performTransaction(accountManager, () -> accountManager.grantAccessLevel("ROLE_ADMIN", login, cardNumber));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator dołącza kolejny poziom dostępu do już istniejącego konta,
     * które danego poziomu dostępu jeszcze nie ma.
     *
     * @param login           login użytkownika, któremu nadajemy poziom dostępu.
     * @param workPhoneNumber numer telefonu służbowego przypisany do poziomu dostępu pracownik.
     * @return odpowiedź z kodem 200 w przypadku pomyślnego dodania nowego poziomu dostępu,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (poziom dostępu jest już przyznany, konto nie istnieje)
     */
    @PUT
    @Path("add-employee-access-level")
    @RolesAllowed("grantEmployeeAccessLevel")
    public Response grantEmployeeAccessLevel(@HeaderParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login,
                                             @HeaderParam("param") @NotBlank @Size(min = 9, max = 9) @Pattern(regexp = RegexPatterns.PHONE_NUMBER) String workPhoneNumber) {
        try {
            performTransaction(accountManager, () -> accountManager.grantAccessLevel("ROLE_EMPLOYEE", login, workPhoneNumber));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator dołącza kolejny poziom dostępu do już istniejącego konta,
     * które danego poziomu dostępu jeszcze nie ma.
     *
     * @param login       login użytkownika, któremu nadajemy poziom dostępu.
     * @param phoneNumber numer telefonu przypisany do poziomu dostępu klient.
     * @return odpowiedź z kodem 200 w przypadku pomyślnego dodania nowego poziomu dostępu,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (poziom dostępu jest już przyznany, konto nie istnieje)
     */
    @PUT
    @Path("add-customer-access-level")
    @RolesAllowed("grantCustomerAccessLevel")
    public Response grantCustomerAccessLevel(@HeaderParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login,
                                             @HeaderParam("param") @NotBlank @Size(min = 9, max = 9) @Pattern(regexp = RegexPatterns.PHONE_NUMBER) String phoneNumber) {
        try {
            performTransaction(accountManager, () -> accountManager.grantAccessLevel("ROLE_CUSTOMER", login, phoneNumber));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator odłącza poziom dostępu od istniejącego konta, pod warunkiem że odłączany poziom dostępu
     * nie jest ostatnim poziomem dostępu danego użytkownika.
     *
     * @param login       login użytkownika, któremu odbieramy poziom dostępu.
     * @param accessLevel poziom dostępu, który chcemy odebrać.
     * @return odpowiedź z kodem 200 w przypadku pomyślnego odebrania poziomu dostępu,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (poziom dostępu jest już odebrany, konto nie istnieje)
     */
    @PUT
    @Path("revoke-access-level")
    @RolesAllowed("revokeAccessLevel")
    public Response revokeAccessLevel(@HeaderParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login,
                                      @HeaderParam("param") @NotBlank String accessLevel) {
        try {
            performTransaction(accountManager, () -> accountManager.revokeAccessLevel(login, accessLevel));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administratorowi wyświetla się lista wszystkich użytkowników w systemie pokazująca informacje o imieniu,
     * nazwisku, loginie, adresie e-mail, aktywności, weryfikacji. Lista udostępnia operacje zablokowania konta,
     * ponowne wysłanie linku weryfikacyjnego, wyświetlanie szczegółowych danych, oraz wyszukiwanie po imieniu i
     * nazwisku.
     *
     * @return odpowiedź z kodem 200 oraz lista wszystkich danych użytkowników w postaci JSON.
     */
    @GET
    @RolesAllowed("getAllAccounts")
    public Response getAllAccounts() {
        try {
            List<AccountEntity> allAccounts = (List<AccountEntity>) performTransaction(accountManager, () -> accountManager.getAllAccounts());
            return Response.ok(
                    allAccounts
                            .stream()
                            .map(AccountDto::fromAccountEntity)
                            .collect(Collectors.toList()))
                    .build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administratorowi wyświetlają się szczegóły konta innego użytkownika.
     *
     * @param login przekazany w żądaniu login użytkownika którego dane chcemy otrzymać.
     * @return odpowiedź z kodem 200 w przypadku pomyślnego pobrania danych użytkownika,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (konto nie istniej lub
     * nie powiodła się operacja odszyfrowania danych wersji i identyfikatora)
     */
    @GET
    @Path("details")
    @RolesAllowed("getAccountDetails")
    public Response getAccountDetails(@QueryParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login) {
        try {
            EditAccountDto editAccountDto = this.getAccountDetailsDto(login);
            return Response.ok(editAccountDto).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Użytkownik przegląda szczegóły swojego konta.
     *
     * @return odpowiedź z kodem 200 w przypadku pomyślnego pobrania danych użytkownika,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (konto nie istniej lub
     * nie powiodła się operacja odszyfrowania danych wersji i identyfikatora)
     */
    @GET
    @Path("own-details")
    @RolesAllowed("getOwnAccountDetails")
    public Response getOwnAccountDetails() {
        try {
            String login = securityContext.getCallerPrincipal().getName();
            EditAccountDto editAccountDto = this.getAccountDetailsDto(login);
            return Response.ok(editAccountDto).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Użytkownik zmienia hasło dostępowe do swojego konta podając poprawne stare hasło oraz dwukrotnie nowe hasło.
     *
     * @param oldPassword  przekazane w nagłówku żądania aktualne hasło użytkownika.
     * @param newPassword  przekazane w nagłówku żądania docelowe hasło użytkownika.
     * @param captchaToken token pochodzący z mechanizmu reCAPTCHA
     * @return odpowiedź z kodem 200, jeśli nowe hasło spełnia wymagania złożoności, stare hasło jest poprawne i
     * operacja powiedzie się lub
     * odpowiedź z kodem 400, jeśli użytkownik podał nieprawidłowe aktualne hasło lub nowe hasło nie spełnia wymagań
     * złożoności lub operacja nie powiodła się.
     */
    @PUT
    @Path("change-own-password")
    @RolesAllowed("changeOwnPassword")
    public Response changeOwnPassword(@HeaderParam("oldPassword") @NotBlank @Size(min = 8) @Pattern(regexp = RegexPatterns.PASSWORD) String oldPassword,
                                      @HeaderParam("newPassword") @NotBlank @Size(min = 8) @Pattern(regexp = RegexPatterns.PASSWORD) String newPassword,
                                      @HeaderParam("captchaToken") String captchaToken) {
        try {
            validateCaptchaToken(captchaToken);

            if (oldPassword.equals(newPassword)) {
                throw new NewPasswordSameAsOldPasswordException();
            }

            String login = securityContext.getCallerPrincipal().getName();

            performTransaction(accountManager, () -> accountManager.changeOwnPassword(login, oldPassword, newPassword));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator zmienia hasło innego użytkownika poprzez podanie nowego hasła.
     *
     * @param login       przekazany w parametrze zapytania login użytkownika, którego hasło chcemy zmienić
     * @param newPassword przekazane w nagłówku żądania nowe hasło użytkownika
     * @return odpowiedź z kodem 200, jeśli nowe hasło spełnia wymagania złożoności i operacja powiedzie się lub
     * odpowiedź z kodem 400, jeśli użytkownik o podanym loginie nie istnieje w bazie danych,
     * nowe hasło nie spełnia wymagań złożoności lub operacja nie powiodła się
     */
    @PUT
    @Path("change-password")
    @RolesAllowed("changePassword")
    public Response changePassword(@QueryParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login,
                                   @HeaderParam("newPassword") @NotBlank @Size(min = 8) @Pattern(regexp = RegexPatterns.PASSWORD) String newPassword) {
        try {
            performTransaction(accountManager, () -> accountManager.changePassword(login, newPassword));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator blokuje konto innego użytkownika.
     *
     * @param login przekazane w nagłówku żądania aktualne hasło użytkownika.
     * @return odpowiedź z kodem 200, udało się zablokować konto lub
     * odpowiedź z kodem 400, jeśli konto jest ostatnim aktywnym kontem administratora, konto nie istnieje,
     * konto jest już zablokowane lub jest niezweryfikowane.
     */
    @PUT
    @Path("lock-account")
    @RolesAllowed("lockAccount")
    public Response lockAccount(@QueryParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login) {
        try {
            performTransaction(accountManager, () -> accountManager.lockAccount(login));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator odblokowuje konto innego użytkownika.
     *
     * @param login przekazane w nagłówku żądania aktualne hasło użytkownika.
     * @return odpowiedź z kodem 200, udało się zablokować konto lub
     * odpowiedź z kodem 400, jeśli konto nie istnieje, konto jest już zablokowane lub jest niezweryfikowane.
     */
    @PUT
    @Path("unlock-account")
    @RolesAllowed("unlockAccount")
    public Response unlockAccount(@QueryParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login) {
        try {
            performTransaction(accountManager, () -> accountManager.unlockAccount(login));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administratorowi wyświetla się lista użytkowników w systemie pokazująca informacje o imieniu, nazwisku, loginie,
     * adresie e-mail, aktywności, weryfikacji wyszukane po imieniu i nazwisku.
     *
     * @param phrase Fraza, która może występować w imieniu i nazwisku przypisanym do konta.
     * @return Lista kont zgodna z podanym argumentem.
     */
    @GET
    @Path("filter-accounts-by-full-name")
    @RolesAllowed("getAccountsFilteredByPhraseInFullName")
    public Response getAccountsFilteredByPhraseInFullName(@QueryParam("phrase") @Pattern(regexp = RegexPatterns.NAME_AND_SURNAME) String phrase) {
        try {
            String lowerCasePhrase = phrase.replaceAll("\\s+", "").toLowerCase();
            List<AccountEntity> accountsByPhrase = (List<AccountEntity>) performTransaction(accountManager, () -> accountManager.findByPhraseInFullName(lowerCasePhrase));
            return Response.ok(
                    accountsByPhrase
                            .stream()
                            .map(AccountDto::fromAccountEntity)
                            .collect(Collectors.toList()))
                    .build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Użytkownik zmienia swój adres email. Na nowy adres email zostaje wysłana wiadomość z linkiem do potwierdzenia
     * zmiany adresu email.
     *
     * @param email        przekazane jako parametr żądania docelowy e-mail użytkownika.
     * @param lang         przekazany jako parametr żądania język użytkownika.
     * @param captchaToken token pozyskany z mechanizmu reCAPTCHA
     * @return odpowiedź z kodem 200, jeśli nowy email spełnia wymagania składniowe, taki email nie istnieje już w
     * systemie operacja powiedzie się lub
     * odpowiedź z kodem 400, jeśli nowy e-mail nie spełnia wymagań
     * złożoności, taki e-mail już istnieje w systemie lub operacja się nie powiodła.
     */
    @POST
    @Path("send-email-change-link")
    @RolesAllowed("sendEmailChangeLink")
    public Response sendEmailChangeLink(@QueryParam("email") @Pattern(regexp = RegexPatterns.EMAIL) @Size(max = 50) String email,
                                        @QueryParam("lang") @NotBlank String lang,
                                        @HeaderParam("captchaToken") String captchaToken) {
        try {
            validateCaptchaToken(captchaToken);
            String login = securityContext.getCallerPrincipal().getName();
            performTransaction(accountManager, () -> accountManager.sendEmailForChangeEmail(login, email, lang, false));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator zmienia adres email wybranego użytkownika. Na nowy adres email zostaje wysłana wiadomość z linkiem
     * do potwierdzenia zmiany adresu email użytkownika.
     *
     * @param login przekazany jako parametr żądania login użytkownika, którego e-mail chcemy zmienić
     * @param email przekazany jako parametr żądania nowy adres e-mail użytkownika
     * @param lang  przekazany jako parametr żądania język użytkownika.
     * @return odpowiedź z kodem 200, jeśli nowy email spełnia wymagania składniowe, taki email nie istnieje już w
     * systemie, operacja powiedzie się lub
     * odpowiedź z kodem 400, jeśli nowy e-mail nie spełnia wymagań
     * złożoności, taki e-mail już istnieje w systemie, użytkownik z takim loginem nie istnieje w systemie
     * lub operacja się nie powiodła.
     */
    @POST
    @Path("send-users-email-change-link")
    @RolesAllowed("sendUsersEmailChangeLink")
    public Response sendUsersEmailChangeLink(@QueryParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login,
                                             @QueryParam("email") @Pattern(regexp = RegexPatterns.EMAIL) @Size(max = 50) String email,
                                             @QueryParam("lang") @NotBlank String lang) {
        try {
            performTransaction(accountManager, () -> accountManager.sendEmailForChangeEmail(login, email, lang, true));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Użytkownik używa linku wysłanego w wiadomości email, potwierdzając tym samym zmianę adresu.
     *
     * @param toVerify przekazane jako parametr żądania docelowy e-mail użytkownika.
     * @return odpowiedź z kodem 200 w przypadku pomyślnej zmiany e-maila,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (link już użyty lub spreparowany)
     */

    @PUT
    @Path("change-email")
    @RolesAllowed("changeOwnEmail")
    public Response changeOwnEmail(@QueryParam("toVerify") @NotBlank String toVerify) {
        try {
            performTransaction(accountManager, () -> accountManager.changeEmail(toVerify));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Użytkownik używa linku wysłanego w wiadomości email, ten przenosi go do formularza, w którym wprowadza on
     * nowe hasło.
     *
     * @param token token potwierdzający czy użytkownik ma prawo do resetu hasła..
     * @param newPassword nowe hasło dla konta użytkownika.
     * @return odpowiedź z kodem 200 jeśli operacja wykonała się poprawnie
     * lub 400 jeśli użytkownik nie istnieje, podał nowe hasło nie spełniające
     * wymaga złożoności, token został spreparowany lub stracił ważność
     */
    @POST
    @Path("reset-password")
    @PermitAll
    public Response resetPassword(@NotNull @HeaderParam("token") String token,
                                  @HeaderParam("newPassword") @NotBlank @Size(min = 8) @Pattern(regexp = RegexPatterns.PASSWORD) String newPassword) {
        try {
            token = URLDecoder.decode(token, StandardCharsets.UTF_8);
            String[] data = linkUtils.extractDataFromTimedToken(token);
            linkUtils.validateTimedToken(data);
            performTransaction(accountManager, () -> accountManager.resetPassword(data[0], newPassword));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator wyświetla raport na temat ostatnich uwierzytelnień użytkowników. Raport zawiera informacje o
     * loginie, adresie IP w wersji 4 albo 6 oraz dacie ostatniego uwierzytelnienia.
     *
     * @return Dane do wygenerowania raportu w postaci listy.
     */
    @GET
    @Path("admin-report")
    @RolesAllowed("getAdminReport")
    public Response getAdminReport() {
        try {
            List<AccountEntity> allAccounts = null;
            allAccounts = (List<AccountEntity>) performTransaction(accountManager, () -> accountManager.getAuthenticatedAccounts());
            return Response.ok(
                    allAccounts
                            .stream()
                            .map(AccountReportDto::fromAccountEntity)
                            .collect(Collectors.toList()))
                    .build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Administrator ponownie wysyła link weryfikacyjny do użytkownika..
     *
     * @param login    Login użytkownika do którego ma zostać wysłany link weryfikacyjny.
     * @param language Język w jakim zostanie wysłana treść wiadomości.
     * @return odpowiedź z kodem 200 w przypadku pomyslnego wysłania widomości email
     * odpowiedź z kodem 400 w przypadku niepowodzenia
     */
    @POST
    @Path("send-verification-link")
    @RolesAllowed("sendVerificationLink")
    public Response sendVerificationLink(@QueryParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) String login,
                                         @QueryParam("language") String language) {
        if (language == null || language.isBlank()) {
            language = "en";
        }
        try {
            String finalLanguage = language;
            performTransaction(accountManager, () -> accountManager.sendVerificationLink(login, finalLanguage));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }


    /**
     * Metoda wykonująca operacje edycji danych użytkownika.
     *
     * @param editAccountDto obiekt klasy {@link EditAccountDto} z zedytowanymi danymi użtkownika.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private void performEditAccount(EditAccountDto editAccountDto) throws AppException {
        AdminEntity adminEntity;
        CustomerEntity customerEntity;
        EmployeeEntity employeeEntity;
        try {
            editAccountDto.setVersion(crypt.decrypt(editAccountDto.getVersion()));
            editAccountDto.setId(crypt.decrypt(editAccountDto.getId()));
            AccountEntity accountEntity = EditAccountDto.convertToAccountEntity(editAccountDto);
            if (editAccountDto.getCardNumberDto() != null) {
                CardNumberDto cardNumberDto = editAccountDto.getCardNumberDto();
                cardNumberDto.setId(crypt.decrypt(cardNumberDto.getId()));
                cardNumberDto.setVersion(crypt.decrypt(cardNumberDto.getVersion()));
                editAccountDto.setCardNumberDto(cardNumberDto);
                adminEntity = CardNumberDto.convertToAdminEntity(editAccountDto.getCardNumberDto(), accountEntity);
            } else {
                adminEntity = null;
            }
            if (editAccountDto.getPhoneNumberDto() != null) {
                PhoneNumberDto phoneNumberDto = editAccountDto.getPhoneNumberDto();
                phoneNumberDto.setId(crypt.decrypt(phoneNumberDto.getId()));
                phoneNumberDto.setVersion(crypt.decrypt(phoneNumberDto.getVersion()));
                editAccountDto.setPhoneNumberDto(phoneNumberDto);
                customerEntity = PhoneNumberDto.convertToCustomerEntity(editAccountDto.getPhoneNumberDto(), accountEntity);
            } else {
                customerEntity = null;
            }
            if (editAccountDto.getWorkPhoneNumberDto() != null) {
                WorkPhoneNumberDto workPhoneNumberDto = editAccountDto.getWorkPhoneNumberDto();
                workPhoneNumberDto.setId(crypt.decrypt(workPhoneNumberDto.getId()));
                workPhoneNumberDto.setVersion(crypt.decrypt(workPhoneNumberDto.getVersion()));
                editAccountDto.setWorkPhoneNumberDto(workPhoneNumberDto);
                employeeEntity = WorkPhoneNumberDto.convertToEmployeeEntity(editAccountDto.getWorkPhoneNumberDto(), accountEntity);
            } else {
                employeeEntity = null;
            }
            performTransaction(accountManager, () -> accountManager.editUserDetails(accountEntity, adminEntity, customerEntity, employeeEntity));
        } catch (DecryptException e) {
            throw new InvalidInputException(e);
        }
    }

    /**
     * Metoda pobierająca dane konta użytkownika o podanym loginie.
     *
     * @param login login użytkownika, którego dane chcemy uzyskać.
     * @return obiekt klasy {@link EditAccountDto} z danymi użytkownika.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private EditAccountDto getAccountDetailsDto(String login) throws AppException {
        AccountEntity account = (AccountEntity) performTransaction(accountManager, () -> accountManager.getAccountDetails(login));
        EditAccountDto editAccountDto = EditAccountDto.fromAccountEntity(account);
        try {
            editAccountDto.setVersion(crypt.encrypt(editAccountDto.getVersion()));
            editAccountDto.setId(crypt.encrypt(editAccountDto.getId()));
            if (editAccountDto.getCardNumberDto() != null) {
                CardNumberDto cardNumberDto = editAccountDto.getCardNumberDto();
                cardNumberDto.setId(crypt.encrypt(cardNumberDto.getId()));
                cardNumberDto.setVersion(crypt.encrypt(cardNumberDto.getVersion()));
                editAccountDto.setCardNumberDto(cardNumberDto);
            }
            if (editAccountDto.getPhoneNumberDto() != null) {
                PhoneNumberDto phoneNumberDto = editAccountDto.getPhoneNumberDto();
                phoneNumberDto.setId(crypt.encrypt(phoneNumberDto.getId()));
                phoneNumberDto.setVersion(crypt.encrypt(phoneNumberDto.getVersion()));
                editAccountDto.setPhoneNumberDto(phoneNumberDto);
            }
            if (editAccountDto.getWorkPhoneNumberDto() != null) {
                WorkPhoneNumberDto workPhoneNumberDto = editAccountDto.getWorkPhoneNumberDto();
                workPhoneNumberDto.setId(crypt.encrypt(workPhoneNumberDto.getId()));
                workPhoneNumberDto.setVersion(crypt.encrypt(workPhoneNumberDto.getVersion()));
                editAccountDto.setWorkPhoneNumberDto(workPhoneNumberDto);
            }
            return editAccountDto;
        } catch (EncryptException e) {
            throw new InternalProblemException();
        }
    }

    /**
     * Metoda walidująca żeton Captcha.
     *
     * @param captchaToken żeton Captcha, który ma zostać zwalidowany.
     * @throws InvalidCaptchaTokenException jeśli żeton jest niepoprawny.
     */
    private void validateCaptchaToken(String captchaToken) throws InvalidCaptchaTokenException {
        if (Boolean.parseBoolean(servletContext.getInitParameter("ENABLE_CAPTCHA")) &&
                !captchaUtils.validateToken(captchaToken)) {
            throw new InvalidCaptchaTokenException();
        }
    }

}
