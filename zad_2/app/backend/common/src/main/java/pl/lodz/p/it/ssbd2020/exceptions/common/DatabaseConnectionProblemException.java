package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class DatabaseConnectionProblemException extends AppException {

    private static final String MESSAGE_DATABASE_CONNECTION_PROBLEM = "Database connection error";
    private static final String RESPONSE_INTERNAL_PROBLEM = "error.internalProblem";

    public DatabaseConnectionProblemException() {
        super(MESSAGE_DATABASE_CONNECTION_PROBLEM);
    }

    public DatabaseConnectionProblemException(Throwable e) {
        super(MESSAGE_DATABASE_CONNECTION_PROBLEM, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INTERNAL_PROBLEM);
    }
}
