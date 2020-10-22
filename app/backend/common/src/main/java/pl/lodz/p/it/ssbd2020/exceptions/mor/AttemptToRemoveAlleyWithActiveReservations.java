package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AttemptToRemoveAlleyWithActiveReservations extends AppException {

    private static final String MESSAGE_ATTEMPT_TO_REMOVE_ALLEY_WITH_ACTIVE_RESERVATIONS = "Attempt to remove alley with active reservations";
    private static final String RESPONSE_ATTEMPT_TO_REMOVE_ALLEY_WITH_ACTIVE_RESERVATIONS = "error.attemptToRemoveAlleyWithActiveReservations";

    public AttemptToRemoveAlleyWithActiveReservations(){
        super(MESSAGE_ATTEMPT_TO_REMOVE_ALLEY_WITH_ACTIVE_RESERVATIONS);
    }

    public AttemptToRemoveAlleyWithActiveReservations(Throwable e){
        super(MESSAGE_ATTEMPT_TO_REMOVE_ALLEY_WITH_ACTIVE_RESERVATIONS, e);
    }


    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ATTEMPT_TO_REMOVE_ALLEY_WITH_ACTIVE_RESERVATIONS);
    }
}
