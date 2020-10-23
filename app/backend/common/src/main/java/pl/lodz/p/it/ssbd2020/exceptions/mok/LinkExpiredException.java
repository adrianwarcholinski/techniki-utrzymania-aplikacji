package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class LinkExpiredException extends AppException {

    private static final String MESSAGE_LINK_EXPIRED = "Link expired";
    private static final String RESPONSE_LINK_EXPIRED = "error.linkExpired";

    public LinkExpiredException() {
        super(MESSAGE_LINK_EXPIRED);
    }

    public LinkExpiredException(Throwable e) {
        super(MESSAGE_LINK_EXPIRED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_LINK_EXPIRED);
    }
}
