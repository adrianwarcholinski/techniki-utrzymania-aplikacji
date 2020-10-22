package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class InvalidInputException extends AppException {

    private static final String MESSAGE_INVALID_INPUT = "Invalid input";
    private static final String RESPONSE_INVALID_INPUT = "error.invalidInput";

    public InvalidInputException() {
        super(MESSAGE_INVALID_INPUT);
    }

    public InvalidInputException(Throwable e) {
        super(MESSAGE_INVALID_INPUT, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INVALID_INPUT);
    }
}
