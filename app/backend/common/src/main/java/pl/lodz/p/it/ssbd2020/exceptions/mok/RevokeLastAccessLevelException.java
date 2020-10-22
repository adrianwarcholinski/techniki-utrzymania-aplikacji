package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class RevokeLastAccessLevelException extends AppException {

    private static final String MESSAGE_LAST_ACCESS_LEVEL = "Last access level of account is going to be revoked";
    private static final String RESPONSE_LAST_ACCESS_LEVEL = "error.lastAccessLevel";

    public RevokeLastAccessLevelException() {
        super(MESSAGE_LAST_ACCESS_LEVEL);
    }

    public RevokeLastAccessLevelException(Throwable e) {
        super(MESSAGE_LAST_ACCESS_LEVEL, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_LAST_ACCESS_LEVEL);
    }
}
