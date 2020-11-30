package pl.lodz.p.it.ssbd2020.utils;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ProcessingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa mapujaca nieobsłużone wyjątki,
 * występujace w tarakcie przetwarzania danych z ciała zapytnia HTTP na obiekt typu Dto,
 * na odpowiedź HTTP z kodem 400 i kluczem do internacjonalizacji komuniaktu błędu w warstwie prezentacji
 */
@Provider
public class ProcessingExceptionExceptionMapper implements ExceptionMapper<ProcessingException> {
    /**
     * Klucz do internacjonalizacji komuniaktu błędu w warstwie prezentacji
     */
    private static final String RESPONSE_INVALID_INPUT = "error.invalidInput";

    /**
     * Mwtoda logujaca wystąpienie wyjątku i mapująca go na odpowiedź HTTP o kodzie 400
     * z kluczem do internacjonalizacji komunikatu błędu w ciele odpowiedzi
     *
     * @param e wyjatek do zmapowania
     * @return obiekt typu {@link Response} z kodem odpowiedzi HTTP 400
     */
    @Override
    public Response toResponse(ProcessingException e) {
        Logger.getGlobal().log(Level.INFO,
                String.format("Exception %1$s occurred with message: %2$s,\n caused by %3$s",
                        e.getClass().getName(), e.getMessage(),
                        e.getCause() != null ? e.getCause().getClass().getName() : "null"));
        return Response.status(AppException.DEFAULT_ERROR_STATUS_CODE).entity(RESPONSE_INVALID_INPUT).build();
    }
}
