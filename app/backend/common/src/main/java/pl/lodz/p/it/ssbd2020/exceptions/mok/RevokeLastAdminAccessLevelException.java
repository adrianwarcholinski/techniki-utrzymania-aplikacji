package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class RevokeLastAdminAccessLevelException extends AppException {

    private static final String MESSAGE_LAST_ADMIN = "Last admin access level is going to be revoked";
    private static final String RESPONSE_LAST_ADMIN = "error.lastAdmin";

    public RevokeLastAdminAccessLevelException() {
        super(MESSAGE_LAST_ADMIN);
    }

    public RevokeLastAdminAccessLevelException(Throwable e) {
        super(MESSAGE_LAST_ADMIN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_LAST_ADMIN);
    }
}
