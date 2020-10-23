package pl.lodz.p.it.ssbd2020.utils.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.security.enterprise.SecurityContext;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa której metody zostają wywołane w momencie wywołania klasy do której została dołączona jako interceptor.
 * Wykorzystywana w klasach punktów końcowych.
 */
public class MethodInvocationInterceptor {


    /**
     * Kontekst bezpieczeństwa, który zawiera informacje o tożsamości użytkownika.
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Metoda przechwytująca, wywoływana w momencie wywołania metody biznesowej obserwowanej klasy. Odnotowuje w dzienniku zdarzeń informacje na temat wywołania.
     *
     * @param context kontekst z informacjami o przechwyconym wywołaniu wraz z operacjami, umożliwiającymi metododzie przechwytującej kontrolę nad zachowaniem łańcucha wywołań.
     * @return zwraca wynik wywołania właściwej metody lub wartość null jeśli metoda ta jest typu void.
     * @throws Exception jeśli wywoływana metoda biznesowa zgłosi wyjątek {@link Exception}.
     */
    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        StringBuilder sb = new StringBuilder();

        Principal principal = securityContext.getCallerPrincipal();
        String login = principal != null ? principal.getName() : "UNAUTHENTICATED";

        sb.append(String.format("Method invoked: %s.%s by user %s",
                context.getTarget().getClass().getName(),
                context.getMethod().getName(),
                login));


        try {
            Object returnedValue = context.proceed();
            return returnedValue;
        } catch (Exception e) {
            sb.append(String.format(" Exception occured: %s ", e));
            throw e;
        } finally {
            Logger.getGlobal().log(Level.INFO, sb.toString());
        }
    }
}
