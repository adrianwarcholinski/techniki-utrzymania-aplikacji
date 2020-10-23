package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class InternalProblemException extends AppException {

    private static final String MESSAGE_INTERNAL_PROBLEM = "Internal error";
    private static final String RESPONSE_INTERNAL_PROBLEM = "error.internalProblem";

    public InternalProblemException() {
        super(MESSAGE_INTERNAL_PROBLEM);
    }

    public InternalProblemException(Throwable e) {
        super(MESSAGE_INTERNAL_PROBLEM, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INTERNAL_PROBLEM);
    }
}
