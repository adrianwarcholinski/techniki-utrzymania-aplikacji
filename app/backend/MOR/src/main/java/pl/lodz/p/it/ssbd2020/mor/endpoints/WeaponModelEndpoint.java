package pl.lodz.p.it.ssbd2020.mor.endpoints;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.AddWeaponModelDto;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.EditWeaponModelDto;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.ListWeaponModelDto;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.WeaponModelManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.Crypt;
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
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa zawierająca definicje punktów końcowych dotyczących modelów broni.
 */
@Path("weapon-model")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
@Produces("application/json")
public class WeaponModelEndpoint extends Endpoint {

    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z modelami broni.
     */
    @Inject
    private WeaponModelManagerLocal weaponModelManager;

    /**
     * Ziarno, które pozwala na szyfrowanie i deszyfrowanie łańcuchów znaków
     */
    @Inject
    private Crypt crypt;

    /**
     * System zwraca użytkownikowi listę wszystkich modelów broni.
     *
     * @return odpowiedź z kodem 200 w przypadku istnienia aktywnych modelów broni,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-active-weapon-models")
    @RolesAllowed({"ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    public Response getAllActiveWeaponModels() {
        try {
            List<WeaponModelEntity> activeWeaponModelEntityList = (List<WeaponModelEntity>) performTransaction(weaponModelManager,
                    () -> weaponModelManager.getAllActiveWeaponModels());
            return Response.ok(mapToWeaponModelDto(activeWeaponModelEntityList)).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * System zwraca wszystkie aktywne modele broni posiadające aktywne egzemplarze broni.
     *
     * @return odpowiedź z kodem 200 w przypadku istnienia aktywnych modelów broni z aktywnymi egzemplarzami broni,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-active-weapon-models-with-active-weapons")
    @RolesAllowed({"ROLE_CUSTOMER","ROLE_EMPLOYEE"})
    public Response getAllActiveWeaponModelsWithActiveWeapons() {
        try {
            List<WeaponModelEntity> activeWeaponModelEntityList =
                    (List<WeaponModelEntity>) performTransaction(weaponModelManager,
                            () -> weaponModelManager.getAllActiveWeaponModelsWithActiveWeapons());
            return Response.ok(mapToWeaponModelDto(activeWeaponModelEntityList)).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik usuwa model broni.
     *
     * @param name nazwa usuwanego modelu broni.
     * @return odpowiedź z kodem 200 w przypadku pomyslnego usuniecia modelu broni o podanej nazwie,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @DELETE
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response removeWeaponModel(@QueryParam("name")  @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME)
                                          @NotBlank String name) {
        try {
            performTransaction(weaponModelManager, () -> weaponModelManager.removeWeaponModel(name));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Przeglądaj szczegóły modelu broni
     *
     * @param name nazwa poszukiwanego modelu broni.
     * @return odpowiedź z kodem 200 oraz obiektem {@link ListWeaponModelDto} w przypadku istnienia modelu broni o
     * podanej nazwie,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-weapon-model")
    @RolesAllowed({"ROLE_CUSTOMER","ROLE_EMPLOYEE"})
    public Response getWeaponModel(@QueryParam("name") @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME) @NotBlank String name) {
        try {
            WeaponModelEntity entity = (WeaponModelEntity) performTransaction(weaponModelManager,
                    () -> weaponModelManager.getWeaponModel(name));
            EditWeaponModelDto dto = EditWeaponModelDto.convertToDto(entity);
            dto.setId(crypt.encrypt(dto.getId()));
            dto.setVersion(crypt.encrypt(dto.getVersion()));
            return Response.ok().entity(dto).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik dodaje nowy model broni podając jego dane, w tym kategorię.
     *
     * @param addWeaponModelDto obiekt klasy {@link AddWeaponModelDto} transferujący dane nowo powstałego modelu broni
     * @return odpowiedź z kodem 200 w przypadku zakończonego sukcesem dodawania nowego modelu broni,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @POST
    @Path("add-weapon-model")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response addWeaponModel(@Valid AddWeaponModelDto addWeaponModelDto) {
        try {
            WeaponCategoryEntity weaponCategoryEntity = new WeaponCategoryEntity(addWeaponModelDto.getWeaponCategory());
            WeaponModelEntity weaponModelEntity = addWeaponModelDto.convertToEntity(weaponCategoryEntity);
            performTransaction(weaponModelManager, () -> weaponModelManager.addWeaponModel(weaponModelEntity));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik edytuje szczegóły modelu, w tym może wybrać nową kategorię broni.
     *
     * @param editWeaponModelDto obiekt klasy {@link EditWeaponModelDto} z zedytowanymi danymi modelu broni.
     * @return odpowiedź z kodem 200 w przypadku zakończonej sukcesem edycji modelu broni,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @PUT
    @Path("edit-weapon-model")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response editWeaponModel(@Valid EditWeaponModelDto editWeaponModelDto) {
        try {
            WeaponCategoryEntity weaponCategoryEntity = new WeaponCategoryEntity(editWeaponModelDto.getWeaponCategory());
            editWeaponModelDto.setId((crypt.decrypt(editWeaponModelDto.getId())));
            editWeaponModelDto.setVersion(crypt.decrypt(editWeaponModelDto.getVersion()));
            WeaponModelEntity weaponModelEntity = editWeaponModelDto.convertToEntity(weaponCategoryEntity);
            performTransaction(weaponModelManager, () -> weaponModelManager.editWeaponModel(weaponModelEntity));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Metoda konwertująca obiekty klasy {@link WeaponModelEntity} na obiekty klasy {@link ListWeaponModelDto}
     *
     * @param list lista z obiektami klasy {@link WeaponModelEntity}
     * @return lista z obiektami klasy {@link ListWeaponModelDto}
     */
    private List<ListWeaponModelDto> mapToWeaponModelDto(List<WeaponModelEntity> list) {
        return list
                .stream()
                .map(ListWeaponModelDto::fromWeaponModelEntity)
                .collect(Collectors.toList());
    }
}
