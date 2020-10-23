package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class GrantAccessLevelInvalidRoleException extends AppException {

    private static final String MESSAGE_INVALID_ROLE = "Grand invalid access level";
    private static final String RESPONSE_INVALID_ROLE = "error.invalidRole";

    public GrantAccessLevelInvalidRoleException() {
        super(MESSAGE_INVALID_ROLE);
    }

    public GrantAccessLevelInvalidRoleException(Throwable e) {
        super(MESSAGE_INVALID_ROLE, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INVALID_ROLE);
    }
}
