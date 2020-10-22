package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ReservationNumberIsAlreadyTakenException extends AppException {

    private static final String MESSAGE_RESERVATION_NUMBER_IS_ALREADY_TAKEN = "Reservation number is already taken";
    private static final String RESPONSE_RESERVATION_NUMBER_IS_ALREADY_TAKEN = "error.reservationNumberIsAlreadyTaken";

    public ReservationNumberIsAlreadyTakenException(){
        super(MESSAGE_RESERVATION_NUMBER_IS_ALREADY_TAKEN);
    }

    public ReservationNumberIsAlreadyTakenException(Throwable e){
        super(MESSAGE_RESERVATION_NUMBER_IS_ALREADY_TAKEN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_RESERVATION_NUMBER_IS_ALREADY_TAKEN);
    }
}
