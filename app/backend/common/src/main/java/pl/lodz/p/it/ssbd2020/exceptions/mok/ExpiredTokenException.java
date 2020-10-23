package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class ExpiredTokenException extends AppException {

    private static final String MESSAGE_EXPIRED_TOKEN = "Expired token";
    private static final String RESPONSE_EXPIRED_TOKEN = "error.expiredToken";

    public ExpiredTokenException() {
        super(MESSAGE_EXPIRED_TOKEN);
    }

    public ExpiredTokenException(Throwable e) {
        super(MESSAGE_EXPIRED_TOKEN, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_EXPIRED_TOKEN);
    }
}
