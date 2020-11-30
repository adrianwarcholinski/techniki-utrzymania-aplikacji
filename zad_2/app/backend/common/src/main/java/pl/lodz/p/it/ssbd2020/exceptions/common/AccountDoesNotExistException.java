package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AccountDoesNotExistException extends AppException {

    private static final String MESSAGE_ACCOUNT_DOES_NOT_EXIST = "Account does not exist";
    private static final String RESPONSE_ACCOUNT_DOES_NOT_EXIST = "error.accountDoesNotExist";

    public AccountDoesNotExistException() {
        super(MESSAGE_ACCOUNT_DOES_NOT_EXIST);
    }

    public AccountDoesNotExistException(Throwable e) {
        super(MESSAGE_ACCOUNT_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ACCOUNT_DOES_NOT_EXIST);
    }
}
