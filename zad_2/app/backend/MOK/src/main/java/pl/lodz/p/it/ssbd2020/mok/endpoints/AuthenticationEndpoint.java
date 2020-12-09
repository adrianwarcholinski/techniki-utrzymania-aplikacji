package pl.lodz.p.it.ssbd2020.mok.endpoints;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AccountDoesNotExistException;
import pl.lodz.p.it.ssbd2020.exceptions.common.SendingEmailException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.AttemptToLockLastAdminAccountException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.UnauthorizedAccountBlockedException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.UnauthorizedException;
import pl.lodz.p.it.ssbd2020.mok.endpoints.dto.LoginResponseDto;
import pl.lodz.p.it.ssbd2020.mok.managers.interfaces.AccountManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.LastAuthenticationTimes;
import pl.lodz.p.it.ssbd2020.utils.LinkUtils;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;
import pl.lodz.p.it.ssbd2020.utils.endpoint.Endpoint;
import pl.lodz.p.it.ssbd2020.utils.interceptor.MethodInvocationInterceptor;
import pl.lodz.p.it.ssbd2020.utils.jwt.JWTTokenUtils;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa definiująca endpointy REST związane z uwierzytelnieniem.
 */
@Path("auth")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
@Produces("application/json")
public class AuthenticationEndpoint extends Endpoint {
    private long amountOf200 = 0;
    private long amountOf400 = 0;

    @Gauge(unit = "response",
            name = "amount_of_200",
            displayName = "Amount of responses with 200 error code",
            tags = "code=200",
            absolute = true)
    public long getAmountOf200() {
        return amountOf200;
    }

    public void incrementAmountOf200() {
        this.amountOf200++;
    }

    @Gauge(unit = "response",
            name = "amount_of_400",
            displayName = "Amount of responses with 400 error code",
            tags = "code=400",
            absolute = true)
    public long getAmountOf400() {
        return amountOf400;
    }

    public void incrementAmountOf400() {
        this.amountOf400++;
    }

    /**
     * Kontekst serwletu, który pozwala odczytywać parametry z deskryptora web.xml.
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z kontem.
     */
    @Inject
    private AccountManagerLocal accountManager;

    /**
     * Ziarno, które udostępnia metody pomocnicze do tokenów
     */
    @Inject
    private LinkUtils linkUtils;

    /**
     * Ziarno, które służy do odczytu danych z żetonu JWT oraz zapisu danych w postaci żetonu JWT.
     */
    @Inject
    JWTTokenUtils jwtTokenUtils;

    /**
     * Handler pozwalający na skorzystanie z IdentityStore w celu uwierzytelnienia użytkownika.
     */
    @Inject
    private IdentityStoreHandler identityStoreHandler;

    /**
     * Pobiera z deskryptora web.xml maksymalną liczbę prób wykonania transakcji.
     */
    @PostConstruct
    public void init() {
        maxTransactions = Integer.parseInt(servletContext.getInitParameter("RENEW_TRANSACTION_LIMIT"));
    }

    /**
     * Użytkownik loguje się do aplikacji przez podanie właściwego loginu i hasła.
     *
     * @param request  żadanie uwierzytelniającego się użytkownika.
     * @param login    login uwierzytelniającego się użytkownika.
     * @param password hasło uwierzytelniającego się użytkownika.
     * @param language język z przeglądarki uwierzytelniającego się użytkownika.
     * @return Odpowiedź z kodem 200 jeśli użytkownik zalogował się pomyslnie, z objektem json w ciele
     * zawierającym dane o loginie użytkownika, jego rolach oraz czasach ostatniego pomyślnego i niepomyślnego uwierzytelnienie
     * lub
     * Odpowiedź z kodem 401 jesli użytkownik nie uwierzytelnił się pomyślnie
     */
    @POST
    @Path("login")
    @PermitAll
    @Counted(unit = MetricUnits.NONE,
            name = "method_invocation",
            absolute = true,
            displayName = "Method invocation",
            description = "Metrics to show how many times login method was called.",
            tags = "method_invocation=login")
    @Timed(name = "handling_time",
            description = "Time of handling the method",
            unit = MetricUnits.MILLISECONDS,
            tags = "method_handling_time=login",
            absolute = true)
    public Response login(@Context HttpServletRequest request,
                          @HeaderParam("login") @NotBlank @Pattern(regexp = RegexPatterns.LOGIN) @Size(max = 20) String login,
                          @HeaderParam("password") @NotBlank String password,
                          @HeaderParam("language") String language) {
        if (language == null || language.isBlank()) {
            language = "en";
        }
        String finalLanguage = language;
        CredentialValidationResult credentialValidationResult = identityStoreHandler.validate(new UsernamePasswordCredential(login, password));
        try {
            if (credentialValidationResult.getStatus() == CredentialValidationResult.Status.VALID) {
                Principal principal = credentialValidationResult.getCallerPrincipal();
                Set<String> callerGroups = credentialValidationResult.getCallerGroups();
                String ip = request.getRemoteAddr();
                LastAuthenticationTimes lastAuthenticationTimes = (LastAuthenticationTimes) performTransaction(accountManager,
                        () -> accountManager.getLastAuthenticationTimes(principal.getName()));
                LoginResponseDto loginResponseDto = new LoginResponseDto(callerGroups,
                        principal.getName(),
                        lastAuthenticationTimes.getLastUnsuccessfulAuthenticationTime(),
                        lastAuthenticationTimes.getLastSuccessfulAuthenticationTime());

                performTransaction(accountManager, () -> accountManager.handleSuccessfulAuthentication(
                        principal.getName(), ip, finalLanguage, callerGroups.contains("ROLE_ADMIN")));
                Logger.getGlobal().log(Level.INFO,
                        String.format("User %1$s logged to system from ip address %2$s", login, ip));
                NewCookie cookie = jwtTokenUtils.newCookie(principal.getName(), callerGroups);

                incrementAmountOf200();
                return Response.ok(loginResponseDto).cookie(cookie).build();
            } else {
                performTransaction(accountManager, () -> accountManager.handleUnsuccessfulAuthenticationAttempt(login));
                if ((Boolean) performTransaction(accountManager, () -> accountManager.lockAccountAfterFailedAuthentication(login, finalLanguage))) {
                    throw new UnauthorizedAccountBlockedException();
                } else {
                    throw new UnauthorizedException();
                }
            }
        } catch (AccountDoesNotExistException | AttemptToLockLastAdminAccountException | SendingEmailException e) {
            incrementAmountOf400();
            return new UnauthorizedException().getResponse();
        } catch (AppException e) {
            incrementAmountOf400();
            return e.getResponse();
        }
    }

    /**
     * Użytkownik wylogowuje się z aplikacji.
     *
     * @param request żadanie wylogowującego się użytkownika.
     * @return Zwraca obiekt typu {@link Response} zawierający
     * text "Success"
     */
    @GET
    @Path("logout")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    @Counted(unit = MetricUnits.NONE,
            name = "method_invocation",
            absolute = true,
            displayName = "Method invocation",
            description = "Metrics to show how many times logout method was called.",
            tags = "method_invocation=logout")
    @Timed(name = "handling_time",
            description = "Time of handling the method",
            unit = MetricUnits.MILLISECONDS,
            tags = "method_handling_time=logout",
            absolute = true)
    public Response logout(@Context HttpServletRequest request) {
        Logger.getGlobal().log(Level.INFO,
                String.format("User %1$s logged out from system from ip address %2$s", securityContext.getCallerPrincipal().getName(), request.getRemoteAddr()));
        return Response.ok().build();
    }

    /**
     * Gość resetuje hasło do swojego konta podając adres email. Na podany adres email otrzymuje link do formularza,
     * w którym może dokonać resetu hasła.
     *
     * @param email    przekazany w nagłówku żądania email.
     * @param language język przeglądarki.
     * @return Odpowiedź z kodem 200 jeśli użytkownik podał email
     * lub
     * Odpowiedź z kodem 500 jesli wystąpił błąd (serwera pocztowego / szyfrowania)
     */
    @POST
    @Path("send-reset-link")
    @PermitAll
    @Counted(unit = MetricUnits.NONE,
            name = "method_invocation",
            absolute = true,
            displayName = "Method invocation",
            description = "Metrics to show how many times sendResetPasswordEmail method was called.",
            tags = "method_invocation=sendResetPasswordEmail")
    @Timed(name = "handling_time",
            description = "Time of handling the method",
            unit = MetricUnits.MILLISECONDS,
            tags = "method_handling_time=sendResetPasswordEmail",
            absolute = true)
    public Response sendResetPasswordEmail(@HeaderParam("email") @NotBlank @Email(regexp = RegexPatterns.EMAIL) String email,
                                           @HeaderParam("language") String language) {
        try {
            performTransaction(accountManager, () -> accountManager.sendEmailForResetPassword(email, language));
            incrementAmountOf200();
            return Response.ok().build();

        } catch (AccountDoesNotExistException e) {
            incrementAmountOf200();
            return Response.ok().build();
        } catch (AppException e) {
            incrementAmountOf400();
            return e.getResponse();
        }
    }

    /**
     * System weryfikuje ważność linku przesłanego przez użytkownika.
     *
     * @param token przekazany w nagłówku żądania email.
     * @return Odpowiedź z kodem 200 jeśli token jest poprawny i ważny
     * lub
     * Odpowiedź z kodem 403 jesli token został spreparowany lub stracił ważność
     */
    @POST
    @Path("verify-reset-link")
    @PermitAll
    @Counted(unit = MetricUnits.NONE,
            name = "method_invocation",
            absolute = true,
            displayName = "Method invocation",
            description = "Metrics to show how many times verifyLink method was called.",
            tags = "method_invocation=verifyLink")
    @Timed(name = "handling_time",
            description = "Time of handling the method",
            unit = MetricUnits.MILLISECONDS,
            tags = "method_handling_time=verifyLink",
            absolute = true)
    public Response verifyLink(@NotNull @HeaderParam("token") String token) {
        try {
            token = URLDecoder.decode(token, StandardCharsets.UTF_8);
            linkUtils.validateTimedToken(linkUtils.extractDataFromTimedToken(token));
            incrementAmountOf200();
            return Response.ok().build();
        } catch (AppException e) {
            incrementAmountOf400();
            return e.getResponse();
        }
    }
}
