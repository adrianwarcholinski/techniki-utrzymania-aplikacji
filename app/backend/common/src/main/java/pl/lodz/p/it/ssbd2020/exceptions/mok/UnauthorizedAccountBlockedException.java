package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class UnauthorizedAccountBlockedException extends AppException {

    private static final String RESPONSE_UNAUTHORIZED_ACCOUNT_WAS_BLOCKED = "error.unauthorizedAccountWasBlocked";

    public UnauthorizedAccountBlockedException() {
        super();
    }

    @Override
    public Response getResponse() {
        return Response.status(DEFAULT_ERROR_STATUS_CODE)
                .entity(RESPONSE_UNAUTHORIZED_ACCOUNT_WAS_BLOCKED)
                .build();
    }
}
