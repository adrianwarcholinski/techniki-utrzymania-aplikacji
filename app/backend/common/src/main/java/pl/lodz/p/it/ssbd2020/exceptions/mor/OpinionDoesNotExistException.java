package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class OpinionDoesNotExistException extends AppException {

    private static final String MESSAGE_OPINION_DOES_NOT_EXIST = "Opinion does not exists";
    private static final String RESPONSE_OPINION_DOES_NOT_EXIST = "error.opinionDoesNotExists";

    public OpinionDoesNotExistException() {
        super(MESSAGE_OPINION_DOES_NOT_EXIST);
    }

    public OpinionDoesNotExistException(Throwable e) {
        super(MESSAGE_OPINION_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_OPINION_DOES_NOT_EXIST);
    }
}