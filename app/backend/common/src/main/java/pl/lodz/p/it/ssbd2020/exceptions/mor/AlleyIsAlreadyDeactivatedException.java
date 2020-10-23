package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AlleyIsAlreadyDeactivatedException extends AppException {

    private static final String MESSAGE_ALLEY_IS_ALREADY_DEACTIVATED = "Alley is already deactivated";
    private static final String RESPONSE_ALLEY_IS_ALREADY_DEACTIVATED = "error.alleyIsAlreadyDeactivated";

    public AlleyIsAlreadyDeactivatedException() {
        super(MESSAGE_ALLEY_IS_ALREADY_DEACTIVATED);
    }

    public AlleyIsAlreadyDeactivatedException(Throwable e) {
        super(RESPONSE_ALLEY_IS_ALREADY_DEACTIVATED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ALLEY_IS_ALREADY_DEACTIVATED);
    }
}
