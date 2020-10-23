package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponDoesNotExistException extends AppException {
    private static final String MESSAGE_WEAPON__DOES_NOT_EXIST = "Weapon does not exist";
    private static final String RESPONSE_WEAPON_DOES_NOT_EXIST = "error.weaponDoesNotExist";

    public WeaponDoesNotExistException() {
        super(MESSAGE_WEAPON__DOES_NOT_EXIST);
    }

    public WeaponDoesNotExistException(Throwable e) {
        super(MESSAGE_WEAPON__DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_DOES_NOT_EXIST);
    }
}
