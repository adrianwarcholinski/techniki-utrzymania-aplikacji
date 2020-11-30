package pl.lodz.p.it.ssbd2020.mok.endpoints;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.InvalidRoleException;
import pl.lodz.p.it.ssbd2020.utils.interceptor.MethodInvocationInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.security.enterprise.SecurityContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa zawierająca definicję punktu końcowego służącego do zalogowania informacji
 * o zmianie aktualnego poziomu dostępu przez użytkownika
 */
@Path("change-role")
@Interceptors(MethodInvocationInterceptor.class)
@Produces("application/json")
public class ChangeRoleEndpoint {

    /**
     * Kontekst bezpieczeństwa, który dostarcza informacje na temat dozwolonych ról
     * zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Jeżeli użytkownik posiada wiele poziomów dostępu może go zmienić. Po wybraniu nowej roli użytkownikowi
     * wyświetlają się opcje adekwatne do wybranego poziomu dostępu
     *
     * @param request    wstrzyknięty obiekt żądania, który zawiera informacje o adresie IP klienta
     * @param targetRole przekazana w nagłówku żądania docelowa rola użytkownika
     * @return odpowiedź z kodem 200, jeśli użytkownikowi przysługuje docelowy poziom dostępu lub
     * odpowiedź z kodem 403, jeśli użytkownik próbuje zmienić poziom dostępu na dla niego niedozwolony
     */
    @POST
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public Response changeRole(@Context HttpServletRequest request,
                               @HeaderParam("targetRole") String targetRole) {
        try {
            if (securityContext.isCallerInRole(targetRole)) {
                String login = securityContext.getCallerPrincipal().getName();
                String ip = request.getRemoteAddr();

                Logger.getGlobal().log(Level.INFO,
                        String.format("User %1$s changed role to %2$s from address %3$s", login, targetRole, ip));

                return Response.ok().build();
            } else {
                throw new InvalidRoleException();
            }
        } catch (AppException e) {
            return e.getResponse();
        }
    }
}
