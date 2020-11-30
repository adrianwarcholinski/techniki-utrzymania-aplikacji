package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccountIsNotVerifiedException extends AppException {

    private static final String MESSAGE_ACCOUNT_IS_NOT_VERIFIED = "Account is not verified";
    private static final String RESPONSE_ACCOUNT_IS_NOT_VERIFIED = "error.accountIsNotVerified";

    public AccountIsNotVerifiedException() {
        super(MESSAGE_ACCOUNT_IS_NOT_VERIFIED);
    }

    public AccountIsNotVerifiedException(Throwable e) {
        super(MESSAGE_ACCOUNT_IS_NOT_VERIFIED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ACCOUNT_IS_NOT_VERIFIED);
    }
}
