package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccountIsAlreadyVerifiedException extends AppException {

    private static final String MESSAGE_ACCOUNT_IS_ALREADY_VERIFIED = "Account is already verified";
    private static final String RESPONSE_ACCOUNT_IS_ALREADY_VERIFIED = "error.accountIsAlreadyVerified";

    public AccountIsAlreadyVerifiedException() {
        super(MESSAGE_ACCOUNT_IS_ALREADY_VERIFIED);
    }

    public AccountIsAlreadyVerifiedException(Throwable e) {
        super(MESSAGE_ACCOUNT_IS_ALREADY_VERIFIED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ACCOUNT_IS_ALREADY_VERIFIED);
    }
}
