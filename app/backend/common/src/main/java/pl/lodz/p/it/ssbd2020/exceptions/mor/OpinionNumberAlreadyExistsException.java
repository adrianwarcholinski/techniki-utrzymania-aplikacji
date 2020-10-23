package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class OpinionNumberAlreadyExistsException extends AppException {

    private static final String MESSAGE_OPINION_DOES_NOT_EXIST = "Opinion number already exists";
    private static final String RESPONSE_OPINION_DOES_NOT_EXIST = "error.internalProblem";

    public OpinionNumberAlreadyExistsException() {
        super(MESSAGE_OPINION_DOES_NOT_EXIST);
    }

    public OpinionNumberAlreadyExistsException(Throwable e) {
        super(MESSAGE_OPINION_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_OPINION_DOES_NOT_EXIST);
    }
}