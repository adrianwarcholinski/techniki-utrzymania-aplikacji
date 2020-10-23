package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ReservationInProgressException extends AppException {

    private static final String MESSAGE_RESERVATION_IN_PROGRESS = "Reservation is already in progress.";
    private static final String RESPONSE_RESERVATION_IN_PROGRESS = "error.reservationInProgress";

    public ReservationInProgressException() {
        super(MESSAGE_RESERVATION_IN_PROGRESS);
    }

    public ReservationInProgressException(Throwable e) {
        super(MESSAGE_RESERVATION_IN_PROGRESS, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_RESERVATION_IN_PROGRESS);
    }
}
