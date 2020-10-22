package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccessLevelAlreadyExistsException extends AppException {

    private static final String MESSAGE_ACCESS_LEVEL_ALREADY_EXISTS = "Access level already exists";
    private static final String RESPONSE_ACCESS_LEVEL_ALREADY_EXISTS = "error.accessLevelAlreadyExists";

    public AccessLevelAlreadyExistsException() {
        super(MESSAGE_ACCESS_LEVEL_ALREADY_EXISTS);
    }

    public AccessLevelAlreadyExistsException(Throwable e) {
        super(MESSAGE_ACCESS_LEVEL_ALREADY_EXISTS, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ACCESS_LEVEL_ALREADY_EXISTS);
    }
}
