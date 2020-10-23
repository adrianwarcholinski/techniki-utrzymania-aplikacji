package pl.lodz.p.it.ssbd2020.exceptions;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response;

/**
 * Abstrakcyjny wyjątek będący bazą do innych wyjątków w aplikacji.
 */
@ApplicationException(rollback = true)
public abstract class AppException extends Exception {

    /**
     * Stała z kodem odpowiedzi 400.
     */
    public static final int DEFAULT_ERROR_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();

    /**
     * Stała z kodem odpowiedzi 401.
     */
    public static final int UNAUTHORIZED_STATUS_CODE = Response.Status.UNAUTHORIZED.getStatusCode();

    /**
     * Stała z kodem odpowiedzi 403.
     */
    public static final int FORBIDDEN_STATUS_CODE = Response.Status.FORBIDDEN.getStatusCode();


    /**
     * Konstruktor.
     */
    protected AppException() {
        super();
    }

    /**
     * Konstruktor.
     *
     * @param message wiadomość zapisana w wyjątku.
     */
    protected AppException(String message) {
        super(message);
    }

    /**
     * Konstruktor.
     *
     * @param message wiadomość zapisana w wyjątku.
     * @param e       przyczyna wyjątku.
     */
    protected AppException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * Implementacja tej metody abstrakacyjnej zwraca obiekt klasy {@link Response}.
     *
     * @return obiekt klasy {@link Response}
     */
    public abstract Response getResponse();

    /**
     * Metoda zwracająca obiekt klasy {@link Response} z domyślnym kodem błędu
     * oraz z podaną wiadomością.
     *
     * @param message wiadomość odpowiedzi
     * @return obiekt klasy {@link Response} z domyślnym kodem błędu
     */
    protected Response getDefaultResponse(String message) {
        return Response.status(DEFAULT_ERROR_STATUS_CODE)
                .entity(message)
                .build();
    }

}