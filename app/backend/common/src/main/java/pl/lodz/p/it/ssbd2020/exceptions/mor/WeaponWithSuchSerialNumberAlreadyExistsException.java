package pl.lodz.p.it.ssbd2020.exceptions.mor;

import pl.lodz.p.it.ssbd2020.exceptions.AppException;

import javax.ws.rs.core.Response;

public class WeaponWithSuchSerialNumberAlreadyExistsException extends AppException {
    private static final String MESSAGE_WEAPON_WITH_SUCH_SERIAL_NUMBER_EXISTS = "Weapon with such serial number already exists";
    private static final String RESPONSE_WEAPON_WITH_SUCH_SERIAL_NUMBER_EXISTS = "error.weaponWithSuchSerialNumberAlreadyExists";

    public WeaponWithSuchSerialNumberAlreadyExistsException() {
        super(MESSAGE_WEAPON_WITH_SUCH_SERIAL_NUMBER_EXISTS);
    }

    public WeaponWithSuchSerialNumberAlreadyExistsException(Throwable e) {
        super(MESSAGE_WEAPON_WITH_SUCH_SERIAL_NUMBER_EXISTS, e);
    }

    @Override
    public Response getResponse() {
        return getDefaultResponse(RESPONSE_WEAPON_WITH_SUCH_SERIAL_NUMBER_EXISTS);
    }
}
