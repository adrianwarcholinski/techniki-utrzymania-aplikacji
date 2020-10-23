package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class InvalidRoleException extends AppException {

    private static final String MESSAGE_INVALID_ROLE = "Invalid role";
    private static final String RESPONSE_INVALID_ROLE = "error.invalidRole";

    public InvalidRoleException() {
        super(MESSAGE_INVALID_ROLE);
    }

    public InvalidRoleException(Throwable e) {
        super(MESSAGE_INVALID_ROLE, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INVALID_ROLE);
    }
}
