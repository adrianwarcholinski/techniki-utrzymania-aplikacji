package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ReservationDoesNotBelongToTheUserException extends AppException {

    private static final String MESSAGE_RESERVATION_DOES_NOT_BELONGS_TO_THE_USER = "Reservation does not belongs to the user";
    private static final String RESPONSE_RESERVATION_DOES_NOT_BELONGS_TO_THE_USER = "error.reservationDoesNotBelongsToTheUser";

    public ReservationDoesNotBelongToTheUserException() {
        super(MESSAGE_RESERVATION_DOES_NOT_BELONGS_TO_THE_USER);
    }

    public ReservationDoesNotBelongToTheUserException(Throwable e) {
        super(MESSAGE_RESERVATION_DOES_NOT_BELONGS_TO_THE_USER, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_RESERVATION_DOES_NOT_BELONGS_TO_THE_USER);
    }
}