package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class NewEmailSameAsCurrentEmailException extends AppException {

    private static final String MESSAGE_NEW_EMAIL_SAME_AS_CURRENT_EMAIL = "New email is the same as current email";
    private static final String RESPONSE_NEW_EMAIL_SAME_AS_CURRENT_EMAIL = "error.newEmailSameAsCurrentEmail";

    public NewEmailSameAsCurrentEmailException() {
        super(MESSAGE_NEW_EMAIL_SAME_AS_CURRENT_EMAIL);
    }

    public NewEmailSameAsCurrentEmailException(Throwable e) {
        super(MESSAGE_NEW_EMAIL_SAME_AS_CURRENT_EMAIL, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_NEW_EMAIL_SAME_AS_CURRENT_EMAIL);
    }
}
