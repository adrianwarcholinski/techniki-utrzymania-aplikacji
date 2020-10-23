package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponIsAlreadyDeactivatedException extends AppException {

    private static final String MESSAGE_WEAPON_IS_ALREADY_DEACTIVATED = "Weapon is already deactivated";
    private static final String RESPONSE_WEAPON_IS_ALREADY_DEACTIVATED = "error.weaponIsAlreadyDeactivated";

    public WeaponIsAlreadyDeactivatedException() {
        super(MESSAGE_WEAPON_IS_ALREADY_DEACTIVATED);
    }

    public WeaponIsAlreadyDeactivatedException(Throwable e) {
        super(RESPONSE_WEAPON_IS_ALREADY_DEACTIVATED, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_IS_ALREADY_DEACTIVATED);
    }
}
