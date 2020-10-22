package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class PhoneNumberIsAlreadyTakenException extends AppException {

    private static final String MESSAGE_PHONE_NUMBER_IS_ALREADY_TAKEN = "Phone number is already taken";
    private static final String RESPONSE_PHONE_NUMBER_IS_ALREADY_TAKEN = "error.phoneNumberIsAlreadyTaken";

    public PhoneNumberIsAlreadyTakenException() {
        super(MESSAGE_PHONE_NUMBER_IS_ALREADY_TAKEN);
    }

    public PhoneNumberIsAlreadyTakenException(Throwable e) {
        super(MESSAGE_PHONE_NUMBER_IS_ALREADY_TAKEN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_PHONE_NUMBER_IS_ALREADY_TAKEN);
    }
}
