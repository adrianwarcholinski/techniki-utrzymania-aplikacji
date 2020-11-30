package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponModelDoesNotExistException extends AppException {
    private static final String MESSAGE_WEAPON_MODEL_DOES_NOT_EXIST = "Weapon model does not exist";
    private static final String RESPONSE_WEAPON_MODEL_DOES_NOT_EXIST = "error.weaponModelDoesNotExist";


    public WeaponModelDoesNotExistException() {
        super(MESSAGE_WEAPON_MODEL_DOES_NOT_EXIST);
    }

    public WeaponModelDoesNotExistException(Throwable e) {
        super(MESSAGE_WEAPON_MODEL_DOES_NOT_EXIST, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_MODEL_DOES_NOT_EXIST);
    }
}
