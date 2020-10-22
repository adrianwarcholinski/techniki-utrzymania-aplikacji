package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WorkPhoneNumberIsAlreadyTakenException extends AppException {

    private static final String MESSAGE_WORK_PHONE_NUMBER_IS_ALREADY_TAKEN = "Work phone number is already taken";
    private static final String RESPONSE_WORK_PHONE_NUMBER_IS_ALREADY_TAKEN = "error.workPhoneNumberIsAlreadyTaken";

    public WorkPhoneNumberIsAlreadyTakenException() {
        super(MESSAGE_WORK_PHONE_NUMBER_IS_ALREADY_TAKEN);
    }

    public WorkPhoneNumberIsAlreadyTakenException(Throwable e) {
        super(MESSAGE_WORK_PHONE_NUMBER_IS_ALREADY_TAKEN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WORK_PHONE_NUMBER_IS_ALREADY_TAKEN);
    }
}
