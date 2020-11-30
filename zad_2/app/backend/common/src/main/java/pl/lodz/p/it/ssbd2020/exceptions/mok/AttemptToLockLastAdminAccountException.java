package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AttemptToLockLastAdminAccountException extends AppException {

    private static final String MESSAGE_ATTEMPT_TO_LOCK_LAST_ADMIN_ACCOUNT = "Attempt to lock last admin account";
    private static final String RESPONSE_ATTEMPT_TO_LOCK_LAST_ADMIN_ACCOUNT = "error.attemptToLockLastAdminAccount";

    public AttemptToLockLastAdminAccountException() {
        super(MESSAGE_ATTEMPT_TO_LOCK_LAST_ADMIN_ACCOUNT);
    }

    public AttemptToLockLastAdminAccountException(Throwable e) {
        super(MESSAGE_ATTEMPT_TO_LOCK_LAST_ADMIN_ACCOUNT, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ATTEMPT_TO_LOCK_LAST_ADMIN_ACCOUNT);
    }
}
