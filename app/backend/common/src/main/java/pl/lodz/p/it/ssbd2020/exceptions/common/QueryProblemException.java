package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class QueryProblemException extends AppException {

    private static final String MESSAGE_QUERY_PROBLEM = "Query error";
    private static final String RESPONSE_INTERNAL_PROBLEM = "error.internalProblem";

    public QueryProblemException() {
        super(MESSAGE_QUERY_PROBLEM);
    }

    public QueryProblemException(Throwable e) {
        super(MESSAGE_QUERY_PROBLEM, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_INTERNAL_PROBLEM);
    }
}
