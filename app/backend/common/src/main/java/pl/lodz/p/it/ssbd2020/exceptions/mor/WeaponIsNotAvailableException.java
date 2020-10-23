package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponIsNotAvailableException extends AppException {
    private static final String MESSAGE_WEAPON_IS_NOT_AVAILABLE = "Weapon is not available";
    private static final String RESPONSE_WEAPON_IS_NOT_AVAILABLE = "error.weaponIsNotAvailable";

    public WeaponIsNotAvailableException() {
        super(MESSAGE_WEAPON_IS_NOT_AVAILABLE);
    }

    public WeaponIsNotAvailableException(Throwable e) {
        super(MESSAGE_WEAPON_IS_NOT_AVAILABLE, e);
    }


    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_IS_NOT_AVAILABLE);
    }
}
