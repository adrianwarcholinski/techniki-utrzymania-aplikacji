package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ReservationDuringClosingHoursException extends AppException {

    private static final String MESSAGE_RESERVATION_DURING_CLOSING_HOURS = "Reservation during closing hours";
    private static final String RESPONSE_RESERVATION_DURING_CLOSING_HOURS = "error.reservationDuringClosingHours";

    public ReservationDuringClosingHoursException(){
        super(MESSAGE_RESERVATION_DURING_CLOSING_HOURS);
    }

    public ReservationDuringClosingHoursException(Throwable e){
        super(MESSAGE_RESERVATION_DURING_CLOSING_HOURS, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_RESERVATION_DURING_CLOSING_HOURS);
    }
}
