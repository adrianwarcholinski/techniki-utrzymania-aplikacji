package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ReservationIsAlreadyFinishedException extends AppException {

    private static final String MESSAGE_RESERVATION_IS_ALREADY_FINISHED = "Reservation is already finished";
    private static final String RESPONSE_RESERVATION_IS_ALREADY_FINISHED = "error.reservationIsAlreadyFinished";

    public ReservationIsAlreadyFinishedException() {
        super(MESSAGE_RESERVATION_IS_ALREADY_FINISHED);
    }

    public ReservationIsAlreadyFinishedException(Throwable e) {
        super(MESSAGE_RESERVATION_IS_ALREADY_FINISHED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_RESERVATION_IS_ALREADY_FINISHED);
    }
}
