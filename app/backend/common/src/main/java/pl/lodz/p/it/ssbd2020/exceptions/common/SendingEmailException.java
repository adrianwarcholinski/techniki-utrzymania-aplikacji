package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class SendingEmailException extends AppException {

    private static final String MESSAGE_SENDING_EMAIL_EXCEPTION = "Error when sending an email";
    private static final String RESPONSE_INTERNAL_PROBLEM = "error.internalProblem";

    public SendingEmailException() {
        super(MESSAGE_SENDING_EMAIL_EXCEPTION);
    }

    public SendingEmailException(Throwable e) {
        super(MESSAGE_SENDING_EMAIL_EXCEPTION, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INTERNAL_PROBLEM);
    }
}
