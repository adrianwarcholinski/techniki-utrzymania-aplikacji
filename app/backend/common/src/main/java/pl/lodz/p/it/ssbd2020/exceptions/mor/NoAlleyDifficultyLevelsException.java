package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class NoAlleyDifficultyLevelsException extends AppException {

    private static final String MESSAGE_NO_ALLEY_DIFFICULTY_LEVELS = "No alley difficulty levels";
    private static final String RESPONSE_NO_ALLEY_DIFFICULTY_LEVELS = "error.noAlleyDifficultyLevels";

    public NoAlleyDifficultyLevelsException() {
        super(MESSAGE_NO_ALLEY_DIFFICULTY_LEVELS);
    }

    public NoAlleyDifficultyLevelsException(Throwable e) {
        super(MESSAGE_NO_ALLEY_DIFFICULTY_LEVELS, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_NO_ALLEY_DIFFICULTY_LEVELS);
    }
}
