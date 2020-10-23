package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccessLevelAlreadyRevokedException extends AppException {

    private static final String MESSAGE_ACCESS_LEVEL_ALREADY_REVOKED = "Access level is already revoked";
    private static final String RESPONSE_ACCESS_LEVEL_ALREADY_REVOKED = "error.accessLevelAlreadyRevoked";

    public AccessLevelAlreadyRevokedException() {
        super(MESSAGE_ACCESS_LEVEL_ALREADY_REVOKED);
    }

    public AccessLevelAlreadyRevokedException(Throwable e) {
        super(MESSAGE_ACCESS_LEVEL_ALREADY_REVOKED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ACCESS_LEVEL_ALREADY_REVOKED);
    }
}
