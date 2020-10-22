package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class LoginIsAlreadyTakenException extends AppException {

    private static final String MESSAGE_LOGIN_IS_ALREADY_TAKEN = "Login is already taken";
    private static final String RESPONSE_LOGIN_IS_ALREADY_TAKEN = "error.loginIsAlreadyTaken";

    public LoginIsAlreadyTakenException() {
        super(MESSAGE_LOGIN_IS_ALREADY_TAKEN);
    }

    public LoginIsAlreadyTakenException(Throwable e) {
        super(MESSAGE_LOGIN_IS_ALREADY_TAKEN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_LOGIN_IS_ALREADY_TAKEN);
    }
}
