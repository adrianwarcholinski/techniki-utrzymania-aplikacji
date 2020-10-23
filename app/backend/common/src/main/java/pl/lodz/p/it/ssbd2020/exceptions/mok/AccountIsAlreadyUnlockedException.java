package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccountIsAlreadyUnlockedException extends AppException {

    private static final String MESSAGE_ACCOUNT_IS_ALREADY_UNLOCKED = "Account is already unlocked";
    private static final String RESPONSE_ACCOUNT_IS_ALREADY_UNLOCKED = "error.accountIsAlreadyUnlocked";

    public AccountIsAlreadyUnlockedException() {
        super(MESSAGE_ACCOUNT_IS_ALREADY_UNLOCKED);
    }

    public AccountIsAlreadyUnlockedException(Throwable e) {
        super(MESSAGE_ACCOUNT_IS_ALREADY_UNLOCKED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ACCOUNT_IS_ALREADY_UNLOCKED);
    }
}
