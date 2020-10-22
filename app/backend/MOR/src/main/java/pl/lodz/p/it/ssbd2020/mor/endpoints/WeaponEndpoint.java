package pl.lodz.p.it.ssbd2020.mor.endpoints;

import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.AddWeaponDto;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.ListWeaponDto;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.WeaponManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;
import pl.lodz.p.it.ssbd2020.utils.endpoint.Endpoint;
import pl.lodz.p.it.ssbd2020.utils.interceptor.MethodInvocationInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa zawierająca definicje punktów końcowych dotyczących egzemplarzy broni.
 */
@Path("weapon")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
public class WeaponEndpoint extends Endpoint {

    /**
     * Komponent EJB (Menedżer) udostępniający operacje na encjach egzemplarzy broni.
     */
    @Inject
    private WeaponManagerLocal weaponManager;

    /**
     * Pracownikowi wyświetla się lista wszystkich egzemplarzy broni.
     *
     * @return odpowiedź z kodem 200 w przypadku istnienia aktywnych egzemplarzy broni,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-active-weapons")
    @RolesAllowed("getAllActiveWeapons")
    public Response getAllActiveWeapons() {
        try {
            List<WeaponEntity> weapons = (List<WeaponEntity>) performTransaction(weaponManager, () -> weaponManager.getAllActiveWeapons());
            return Response.ok(map(weapons)).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * System zwraca użytkownikowi listę aktywnych egzemplarzy broni należących dla danego modelu broni.
     *
     * @param modelName nazwa modelu broni dla, którego pobierane są egzemplarze broni.
     * @return odpowiedź z kodem 200 w przypadku istnienia aktywnych egzemplarzy broni danego modelu broni,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-active-weapons-by-model-name")
    @RolesAllowed("getAllActiveWeaponsByModelName")
    public Response getAllActiveWeaponsByModelName(@QueryParam("modelName") @NotBlank
                                                   @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME, message =
                                                           "Weapon model name is not valid") String modelName) {
        try {
            List<WeaponEntity> weapons = (List<WeaponEntity>) performTransaction(weaponManager,
                    () -> weaponManager.getAllActiveWeaponsByModelName(modelName));
            return Response.ok(map(weapons)).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik usuwa egzemplarz broni.
     *
     * @param serialNumber numer seryjny broni
     * @return odpowiedź z kodem 200 w przypadku usunięcia egzemplarzu broni lub
     * odpowiedź z kodem 400 kiedy broni została już usunięta
     */
    @DELETE
    @Path("remove")
    @RolesAllowed("removeWeapon")
    public Response removeWeapon(@QueryParam("serialNumber")
                                 @Pattern(regexp = RegexPatterns.WEAPON_SERIAL_NUMBER, message = "serial number is not valid")
                                 @Size(max = 25) String serialNumber) {
        try {
            performTransaction(weaponManager, () -> weaponManager.removeWeapon(serialNumber));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik dodaje nowy egzemplarz broni podając jego dane, w tym model broni.
     *
     * @param weaponDto obiekt klasy {@link AddWeaponDto} z danymi nowo powstałego egzemplarza broni
     * @return odpowiedź z kodem 200 w przypadku dodania egzemplarzu broni lub
     * odpowiedź z kodem 400 kiedy egzemplarz broni o takim numerze seryjnym już instnieje lub nie ma takiego modelu broni.
     */
    @POST
    @Path("create")
    @RolesAllowed("createWeapon")
    public Response createWeapon(@Valid AddWeaponDto weaponDto) {
        try {
            WeaponEntity weaponEntity = new WeaponEntity(weaponDto.getSerialNumber(), null);
            performTransaction(weaponManager, () -> weaponManager.createWeapon(weaponEntity, weaponDto.getWeaponModelName()));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Metoda konwertująca obiekty klasy {@link WeaponEntity} na obiekty klasy {@link ListWeaponDto}
     *
     * @param weapons lista z obiektami klasy {@link WeaponEntity}
     * @return lista z obiektami klasy {@link ListWeaponDto}
     */
    private List<ListWeaponDto> map(List<WeaponEntity> weapons) {
        return weapons.stream()
                .map(ListWeaponDto::map)
                .collect(Collectors.toList());
    }
}
