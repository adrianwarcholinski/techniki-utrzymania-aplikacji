package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class NewPasswordSameAsOldPasswordException extends AppException {

    private static final String MESSAGE_NEW_PASSWORD_SAME_AS_OLD_PASSWORD = "New password is the same as old password";
    private static final String RESPONSE_NEW_PASSWORD_SAME_AS_OLD_PASSWORD = "error.newPasswordSameAsOldPassword";

    public NewPasswordSameAsOldPasswordException() {
        super(MESSAGE_NEW_PASSWORD_SAME_AS_OLD_PASSWORD);
    }

    public NewPasswordSameAsOldPasswordException(Throwable e) {
        super(MESSAGE_NEW_PASSWORD_SAME_AS_OLD_PASSWORD, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_NEW_PASSWORD_SAME_AS_OLD_PASSWORD);
    }
}
