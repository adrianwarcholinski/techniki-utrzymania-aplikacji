package pl.lodz.p.it.ssbd2020.mor.endpoints;

import pl.lodz.p.it.ssbd2020.entities.WeaponCategoryEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.ListWeaponDto;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.WeaponCategoryDto;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.WeaponCategoryManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.endpoint.Endpoint;
import pl.lodz.p.it.ssbd2020.utils.interceptor.MethodInvocationInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa zawierająca definicje punktów końcowych dotyczących kategorii broni.
 */
@Path("weapon-category")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
@Produces("application/json")
public class WeaponCategoryEndpoint extends Endpoint {

    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z egzemplarzami broni.
     */
    @Inject
    private WeaponCategoryManagerLocal weaponCategoryManager;

    /**
     * System zwraca użytkownikowi listę wszystkich kategorii broni.
     *
     * @return odpowiedź z kodem 200 i listą obiektów {@link WeaponCategoryDto} w przypadku istnienia aktywnych kategorii broni,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-all-weapon-categories")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response getAllWeaponCategories() {
        try {
            List<WeaponCategoryEntity> list = (List<WeaponCategoryEntity>) performTransaction(weaponCategoryManager,
                    () -> weaponCategoryManager.getAllWeaponCategories());
            return Response.ok().entity(map(list)).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Metoda konwertująca obiekty klasy {@link WeaponCategoryEntity} na obiekty klasy {@link WeaponCategoryDto}
     *
     * @param weaponCategories lista z obiektami klasy {@link WeaponCategoryEntity}
     * @return lista z obiektami klasy {@link WeaponCategoryDto}
     */
    private List<WeaponCategoryDto> map(List<WeaponCategoryEntity> weaponCategories) {
        return weaponCategories.stream()
                .map(WeaponCategoryDto::map)
                .collect(Collectors.toList());
    }
}
