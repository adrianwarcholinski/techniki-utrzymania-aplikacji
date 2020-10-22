package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AttemptToRemoveWeaponWithActiveReservationException extends AppException {
    private static final String RESPONSE_ATTEMPT_TO_REMOVE_WEAPON_WITH_ACTIVE_RESERVATION_EXCEPTION
            = "error.attemptToRemoveWeaponWithActiveReservation";
    private static final String MESSAGE_ATTEMPT_TO_REMOVE_WEAPON_WITH_ACTIVE_RESERVATION_EXCEPTION
            = "Attempt to remove weapon with active reservation";

    public AttemptToRemoveWeaponWithActiveReservationException() {
        super(MESSAGE_ATTEMPT_TO_REMOVE_WEAPON_WITH_ACTIVE_RESERVATION_EXCEPTION);
    }

    public AttemptToRemoveWeaponWithActiveReservationException(Throwable e) {
        super(MESSAGE_ATTEMPT_TO_REMOVE_WEAPON_WITH_ACTIVE_RESERVATION_EXCEPTION, e);
    }


    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ATTEMPT_TO_REMOVE_WEAPON_WITH_ACTIVE_RESERVATION_EXCEPTION);
    }
}
