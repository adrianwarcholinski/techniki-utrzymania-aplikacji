package pl.lodz.p.it.ssbd2020.mok.endpoints;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.mok.managers.interfaces.AccountManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.EmailCreator;
import pl.lodz.p.it.ssbd2020.utils.EmailSender;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;
import pl.lodz.p.it.ssbd2020.utils.endpoint.Endpoint;
import pl.lodz.p.it.ssbd2020.utils.interceptor.MethodInvocationInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.mail.MessagingException;
import javax.security.enterprise.SecurityContext;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa zawierająca definicję punktu końcowego służącego do wysyłania pomocniczych maili
 */
@Path("send-email")
@Interceptors(MethodInvocationInterceptor.class)
@Produces("application/json")
public class SendingEmailEndpoint extends Endpoint {

    /**
     * Kontekst bezpieczeństwa, który dostarcza informacje na temat loginu
     * zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Ziarno, które pozwala na wysłanie wiadomości e-mail.
     */
    @Inject
    private EmailSender emailSender;

    /**
     * Ziarno, które pozwala na wygenerowanie wiadomości email.
     */
    @Inject
    private EmailCreator emailCreator;

    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z kontem.
     */
    @Inject
    private AccountManagerLocal accountManager;

    /**
     * System informuje użytkownika o zmianie aktywności jego konta.
     *
     * @param language   język z jakim ma być wysłana wiadomość
     * @param isBlocking flaga do rozróżnienia  czy operacja to blokowanie/odblokowanie
     * @param email      adres email na jaki ma zostać  wysłana wiadomość
     * @return odpowiedź z kodem 200, niezależnie od wyniku metody
     */
    @POST
    @RolesAllowed("activityChangedNotification")
    public Response activityChangedNotification(@HeaderParam("language") String language,
                                                @NotNull @HeaderParam("isBlocking") boolean isBlocking,
                                                @NotNull @HeaderParam("email") @Email(regexp = RegexPatterns.EMAIL) @Size(max = 50) String email) {
        if (language == null || language.isBlank()) {
            language = "en";
        }
        try {
            if (!(boolean) performTransaction(accountManager, () -> accountManager.isVerifiedAccountWithEmail(email))) {
                return Response.ok().build();
            }
            String login = securityContext.getCallerPrincipal().getName();
            try {
                if (isBlocking) {
                    emailSender.sendEmail(emailCreator.getLockAccountEmail(language, email));
                    Logger.getGlobal().log(Level.INFO,
                            String.format("Notification about lock account has been sent by administrator: %1$s to the email address: %2$s ", login, email));
                } else {
                    emailSender.sendEmail(emailCreator.getUnlockAccountEmail(language, email));
                    Logger.getGlobal().log(Level.INFO,
                            String.format("Notification about unlock account has been sent by administrator: %1$s to the email address: %2$s ", login, email));
                }
            } catch (MessagingException e) {
                Logger.getGlobal().log(Level.INFO, String.format("Exception occured: %s", e));
            }
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }
}
