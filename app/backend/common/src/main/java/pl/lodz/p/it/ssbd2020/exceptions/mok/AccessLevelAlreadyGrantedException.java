package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccessLevelAlreadyGrantedException extends AppException {

    private static final String MESSAGE_ACCESS_LEVEL_ALREADY_GRANTED = "Access level is already granted";
    private static final String RESPONSE_ACCESS_LEVEL_ALREADY_GRANTED = "error.accessLevelAlreadyGranted";

    public AccessLevelAlreadyGrantedException() {
        super(MESSAGE_ACCESS_LEVEL_ALREADY_GRANTED);
    }

    public AccessLevelAlreadyGrantedException(Throwable e) {
        super(MESSAGE_ACCESS_LEVEL_ALREADY_GRANTED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ACCESS_LEVEL_ALREADY_GRANTED);
    }
}
