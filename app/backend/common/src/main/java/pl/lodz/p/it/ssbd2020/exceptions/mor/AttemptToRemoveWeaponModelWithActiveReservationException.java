package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AttemptToRemoveWeaponModelWithActiveReservationException extends AppException {

    private static final String RESPONSE_ATTEMPT_TO_REMOVE_WEAPON_MODEL_WITH_ACTIVE_RESERVATION_EXCEPTION
            = "error.attemptToRemoveWeaponModelWithActiveReservation";
    private static final String MESSAGE_ATTEMPT_TO_REMOVE_WEAPON_MODEL_WITH_ACTIVE_RESERVATION_EXCEPTION
            = "Attempt to remove weapon model with active reservation";

    public AttemptToRemoveWeaponModelWithActiveReservationException() {
        super(MESSAGE_ATTEMPT_TO_REMOVE_WEAPON_MODEL_WITH_ACTIVE_RESERVATION_EXCEPTION);
    }

    public AttemptToRemoveWeaponModelWithActiveReservationException(Throwable e) {
        super(MESSAGE_ATTEMPT_TO_REMOVE_WEAPON_MODEL_WITH_ACTIVE_RESERVATION_EXCEPTION, e);
    }


    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ATTEMPT_TO_REMOVE_WEAPON_MODEL_WITH_ACTIVE_RESERVATION_EXCEPTION);
    }
}
