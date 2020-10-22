package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccountIsAlreadyLockedException extends AppException {

    private static final String MESSAGE_ACCOUNT_IS_ALREADY_LOCKED = "Account is already locked";
    private static final String RESPONSE_ACCOUNT_IS_ALREADY_LOCKED = "error.accountIsAlreadyLocked";

    public AccountIsAlreadyLockedException() {
        super(MESSAGE_ACCOUNT_IS_ALREADY_LOCKED);
    }

    public AccountIsAlreadyLockedException(Throwable e) {
        super(MESSAGE_ACCOUNT_IS_ALREADY_LOCKED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ACCOUNT_IS_ALREADY_LOCKED);
    }
}
