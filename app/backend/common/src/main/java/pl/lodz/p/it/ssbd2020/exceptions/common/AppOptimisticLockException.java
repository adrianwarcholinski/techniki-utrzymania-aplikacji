package pl.lodz.p.it.ssbd2020.exceptions.common;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AppOptimisticLockException extends AppException {

    private static final String RESPONSE_OPTIMISTIC_LOCK_EXCEPTION = "error.optimisticLock";
    private static final String MESSAGE_OPTIMISTIC_LOCK_EXCEPTION = "Optimistic lock error";

    public AppOptimisticLockException() {
        super(MESSAGE_OPTIMISTIC_LOCK_EXCEPTION);
    }

    public AppOptimisticLockException(Throwable e) {
        super(MESSAGE_OPTIMISTIC_LOCK_EXCEPTION, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_OPTIMISTIC_LOCK_EXCEPTION);
    }
}
