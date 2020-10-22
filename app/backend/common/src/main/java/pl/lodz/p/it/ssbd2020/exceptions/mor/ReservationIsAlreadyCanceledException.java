package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ReservationIsAlreadyCanceledException extends AppException {

    private static final String MESSAGE_RESERVATION_IS_ALREADY_CANCELED = "Reservation is already canceled";
    private static final String RESPONSE_RESERVATION_IS_ALREADY_CANCELED = "error.reservationIsAlreadyCanceled";

    public ReservationIsAlreadyCanceledException() {
        super(MESSAGE_RESERVATION_IS_ALREADY_CANCELED);
    }

    public ReservationIsAlreadyCanceledException(Throwable e) {
        super(MESSAGE_RESERVATION_IS_ALREADY_CANCELED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_RESERVATION_IS_ALREADY_CANCELED);
    }
}
