package pl.lodz.p.it.ssbd2020.exceptions.mok;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class LinkCorruptedException extends AppException {

    private static final String MESSAGE_LINK_CORRUPTED = "Link corrupted";
    private static final String RESPONSE_LINK_CORRUPTED = "error.linkCorrupted";

    public LinkCorruptedException() {
        super(MESSAGE_LINK_CORRUPTED);
    }

    public LinkCorruptedException(Throwable e) {
        super(MESSAGE_LINK_CORRUPTED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_LINK_CORRUPTED);
    }
}
