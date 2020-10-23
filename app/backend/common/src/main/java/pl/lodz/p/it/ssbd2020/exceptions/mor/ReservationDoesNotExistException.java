package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ReservationDoesNotExistException extends AppException {

    private static final String MESSAGE_RESERVATION_DOES_NOT_EXIST = "Reservation does not exist";
    private static final String RESPONSE_RESERVATION_DOES_NOT_EXIST = "error.reservationDoesNotExist";

    public ReservationDoesNotExistException() {
        super(MESSAGE_RESERVATION_DOES_NOT_EXIST);
    }

    public ReservationDoesNotExistException(Throwable e) {
        super(MESSAGE_RESERVATION_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_RESERVATION_DOES_NOT_EXIST);
    }
}