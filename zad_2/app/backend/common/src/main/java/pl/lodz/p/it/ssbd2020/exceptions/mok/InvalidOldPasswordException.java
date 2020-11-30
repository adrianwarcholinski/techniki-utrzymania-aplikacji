package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class InvalidOldPasswordException extends AppException {

    private static final String MESSAGE_INVALID_OLD_PASSWORD = "Invalid old password";
    private static final String RESPONSE_INVALID_OLD_PASSWORD = "error.invalidOldPassword";

    public InvalidOldPasswordException() {
        super(MESSAGE_INVALID_OLD_PASSWORD);
    }

    public InvalidOldPasswordException(Throwable e) {
        super(MESSAGE_INVALID_OLD_PASSWORD, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INVALID_OLD_PASSWORD);
    }
}
