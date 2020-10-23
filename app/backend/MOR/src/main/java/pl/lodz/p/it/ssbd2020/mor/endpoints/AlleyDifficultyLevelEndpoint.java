package pl.lodz.p.it.ssbd2020.mor.endpoints;

import pl.lodz.p.it.ssbd2020.entities.AlleyDifficultyLevelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.AlleyDifficultyLevelManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.endpoint.Endpoint;
import pl.lodz.p.it.ssbd2020.utils.interceptor.MethodInvocationInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa zawierająca definicje punktów końcowych dotyczących poziomów trudności torów.
 */
@Path("alley-difficulty-level")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
public class AlleyDifficultyLevelEndpoint extends Endpoint {

    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z poziomami trudności toru.
     */
    @Inject
    private AlleyDifficultyLevelManagerLocal alleyDifficultyLevelManager;

    /**
     * System zwraca użytkownikowi poziomy trudności toru potrzebne w procesie dodawania nowego toru.
     *
     * @return odpowiedź z kodem 200 w przypadku istnienia przynajmniej jednego poziomu trudności toru,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-alley-difficulty-levels")
    @RolesAllowed("getAllAlleyDifficultyLevels")
    public Response getAllAlleyDifficultyLevels() {
        try {
            List<AlleyDifficultyLevelEntity> allAlleyDifficultyLevels = (List<AlleyDifficultyLevelEntity>) performTransaction(alleyDifficultyLevelManager,
                    () -> alleyDifficultyLevelManager.getAllAlleyDifficultyLevels());
            return Response.ok(map(allAlleyDifficultyLevels)).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }
    /**
     * Metoda konwertująca obiekty klasy {@link AlleyDifficultyLevelEntity} na obiekty klasy {@link String}
     *
     * @param list lista z obiektami klasy {@link AlleyDifficultyLevelEntity}
     * @return lista z obiektami klasy {@link String}
     */
    private List<String> map(List<AlleyDifficultyLevelEntity> list) {
        return list.stream().
                map(AlleyDifficultyLevelEntity::getName).
                collect(Collectors.toList());
    }


}
