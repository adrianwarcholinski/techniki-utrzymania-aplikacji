package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AlleyIsNotAvailableException extends AppException {

    private static final String MESSAGE_ALLEY_IS_NOT_AVAILABLE = "Alley is not available";
    private static final String RESPONSE_ALLEY_IS_NOT_AVAILABLE = "error.alleyIsNotAvailable";

    public AlleyIsNotAvailableException(){
        super(MESSAGE_ALLEY_IS_NOT_AVAILABLE);
    }

    public AlleyIsNotAvailableException(Throwable e){
        super(MESSAGE_ALLEY_IS_NOT_AVAILABLE, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ALLEY_IS_NOT_AVAILABLE);
    }
}
