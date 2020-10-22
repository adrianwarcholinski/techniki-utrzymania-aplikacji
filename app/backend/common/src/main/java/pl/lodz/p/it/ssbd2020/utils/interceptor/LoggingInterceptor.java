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
 * Wykorzystywana w fasadach i menedżerach.
 */
public class LoggingInterceptor {

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

        appendMethodInvocationInfo(sb, context);
        appendParamsInfo(sb, context);

        try {
            long startTime = System.currentTimeMillis();
            Object returnedValue = context.proceed();
            long duration = System.currentTimeMillis() - startTime;
            sb.append(String.format(" Execution took: %s ms", duration));
            appendReturnedValueInfo(sb, returnedValue);
            return returnedValue;
        } catch (Exception e) {
            sb.append(String.format(" Exception occured: %s", e.getClass()));
            if (e.getCause() != null) {
                sb.append(String.format(" caused by %s", e.getCause().getClass()));
            }
            throw e;
        } finally {
            Logger.getGlobal().log(Level.INFO, sb.toString());
        }
    }

    /**
     * Metoda odpowiedzialna za odnotowanie wartości podstawowych przechwyconego wywołania metody.
     *
     * @param sb      obiekt klasy {@link StringBuilder} przekazywany ze wcześniej dodanym komunikatami na temat wywołania
     * @param context kontekst z informacjami o przechwyconym wywołaniu wraz z operacjami, umożliwiającymi metododzie przechwytującej kontrolę nad zachowaniem łańcucha wywołań.
     */
    private void appendMethodInvocationInfo(StringBuilder sb, InvocationContext context) {
        Principal principal = securityContext.getCallerPrincipal();
        String login = principal != null ? principal.getName() : "UNAUTHENTICATED";

        sb.append(String.format("Method invoked: %s.%s by user %s",
                context.getTarget().getClass().getName(),
                context.getMethod().getName(),
                login));
    }

    /**
     * Metoda odpowiedzialna za odnotowanie wartości parametrów przechwyconego wywołania metody.
     *
     * @param sb      obiekt klasy {@link StringBuilder} przekazywany ze wcześniej dodanym komunikatami na temat wywołania
     * @param context kontekst z informacjami o przechwyconym wywołaniu wraz z operacjami, umożliwiającymi metododzie przechwytującej kontrolę nad zachowaniem łańcucha wywołań.
     */
    private void appendParamsInfo(StringBuilder sb, InvocationContext context) {
        Object[] params = context.getParameters();
        if (params != null) {
            for (Object param : params) {
                String paramMessage = param != null ?
                        String.format(" with parameter %s", param.toString()) :
                        " with null parameter";
                sb.append(paramMessage);
            }
        }
    }

    /**
     * Metoda odpowiedzialna za odnotowanie wartości jaką zwraca przechwycone wywołanie metody.
     *
     * @param sb            obiekt klasy {@link StringBuilder} przekazywany ze wcześniej dodanym komunikatami na temat wywołania
     * @param returnedValue wartość jaką zwróciło wywołanie następnej metody.
     */
    private void appendReturnedValueInfo(StringBuilder sb, Object returnedValue) {
        String returnedValueMessage = returnedValue != null ?
                String.format(" returned %s ", returnedValue.toString()) :
                " returned null";

        sb.append(returnedValueMessage);
    }
}
