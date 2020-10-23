package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ReservationIsTooLongException extends AppException {

    private static final String MESSAGE_RESERVATION_IS_TOO_LONG = "Reservation is too long";
    private static final String RESPONSE_RESERVATION_IS_TOO_LONG = "error.reservationIsTooLong";


    public ReservationIsTooLongException() {
        super(MESSAGE_RESERVATION_IS_TOO_LONG);
    }
    public ReservationIsTooLongException(Throwable e) {
        super(MESSAGE_RESERVATION_IS_TOO_LONG, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_RESERVATION_IS_TOO_LONG);
    }

}
