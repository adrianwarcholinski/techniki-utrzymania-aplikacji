package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class AlleyDifficultyLevelDoesNotExistException extends AppException {

    private static final String MESSAGE_ALLEY_DIFFICULTY_LEVEL_DOES_NOT_EXIST = "Alley difficulty level does not exist";
    private static final String RESPONSE_ALLEY_DIFFICULTY_LEVEL_DOES_NOT_EXIST = "error.alleyDifficultyLevelDoesNotExist";

    public AlleyDifficultyLevelDoesNotExistException() {
        super(MESSAGE_ALLEY_DIFFICULTY_LEVEL_DOES_NOT_EXIST);
    }

    public AlleyDifficultyLevelDoesNotExistException(Throwable e) {
        super(MESSAGE_ALLEY_DIFFICULTY_LEVEL_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_ALLEY_DIFFICULTY_LEVEL_DOES_NOT_EXIST);
    }
}