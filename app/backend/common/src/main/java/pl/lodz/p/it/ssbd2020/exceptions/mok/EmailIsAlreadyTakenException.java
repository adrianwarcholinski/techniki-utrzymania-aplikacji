package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class EmailIsAlreadyTakenException extends AppException {

    private static final String MESSAGE_EMAIL_IS_ALREADY_TAKEN = "Email is already taken";
    private static final String RESPONSE_EMAIL_IS_ALREADY_TAKEN = "error.emailIsAlreadyTaken";

    public EmailIsAlreadyTakenException() {
        super(MESSAGE_EMAIL_IS_ALREADY_TAKEN);
    }

    public EmailIsAlreadyTakenException(Throwable e) {
        super(MESSAGE_EMAIL_IS_ALREADY_TAKEN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_EMAIL_IS_ALREADY_TAKEN);
    }
}
