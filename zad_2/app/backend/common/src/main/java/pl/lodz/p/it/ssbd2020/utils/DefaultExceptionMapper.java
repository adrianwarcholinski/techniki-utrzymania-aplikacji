package pl.lodz.p.it.ssbd2020.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa mapujaca nieobsłużone wyjątki z aplkacji na odpowiedź HTTP o kodzie 500
 */

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    /**
     * Metoda logujaca wystąpienie wyjątku i mapująca go na odpowiedź HTTP
     *
     * @param e wyjatek do zmapowania
     * @return obiekt typu {@link Response} z odpowiedzią HTTP
     */
    @Override
    public Response toResponse(Exception e) {
        Logger.getGlobal().log(Level.INFO,
                String.format("Exception %1$s occurred with message: %2$s,\n caused by %3$s",
                        e.getClass().getName(), e.getMessage(),
                        e.getCause() != null ? e.getCause().getClass().getName() : "null"));
        if (e instanceof WebApplicationException)
            return ((WebApplicationException) e).getResponse();
        else
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
    }
}
