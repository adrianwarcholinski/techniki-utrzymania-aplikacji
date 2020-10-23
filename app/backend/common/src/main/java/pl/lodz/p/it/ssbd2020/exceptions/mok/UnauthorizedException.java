package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class UnauthorizedException extends AppException {

    public UnauthorizedException() {
        super();
    }

    @Override
    public Response getResponse() {
        return Response.status(DEFAULT_ERROR_STATUS_CODE).build();
    }
}
