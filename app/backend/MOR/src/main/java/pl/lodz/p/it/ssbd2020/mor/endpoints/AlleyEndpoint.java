package pl.lodz.p.it.ssbd2020.mor.endpoints;

import pl.lodz.p.it.ssbd2020.entities.AlleyDifficultyLevelEntity;
import pl.lodz.p.it.ssbd2020.entities.AlleyEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InternalProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.DecryptException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.EncryptException;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.AddAlleyDto;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.EditAlleyDto;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.ListAlleyDto;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.AlleyDifficultyLevelManagerLocal;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.AlleyManagerLocal;
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
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa zawierająca definicje punktów końcowych dotyczących torów.
 */
@Path("alley")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
@Produces("application/json")
public class AlleyEndpoint extends Endpoint {
    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z torami.
     */
    @Inject
    private AlleyManagerLocal alleyManager;

    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z poziomami trudności torów.
     */
    @Inject
    private AlleyDifficultyLevelManagerLocal alleyDifficultyLevelManager;

    /**
     * Ziarno, które pozwala na szyfrowanie i deszyfrowanie łańcuchów znaków
     */
    @Inject
    private Crypt crypt;

    /**
     * Pracownik lub klient przegląda szczegóły toru.
     *
     * @param name przekazana w żądaniu nazwa toru którego dane chcemy otrzymać.
     * @return odpowiedź z kodem 200 w przypadku pomyślnego pobrania danych toru,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (tor nie istniej lub
     * nie powiodła się operacja odszyfrowania danych wersji i identyfikatora)
     */
    @GET
    @Path("details")
    @RolesAllowed({"ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public Response getAlleyDetails(@QueryParam("name") @NotBlank @Size(max = 50)
                                    @Pattern(regexp = RegexPatterns.ALLEY_NAME) String name) {
        try {
            EditAlleyDto editAlleyDto = this.getAlleyDetailsDto(name);
            List<AlleyDifficultyLevelEntity> allAlleyDifficultyLevels = (List<AlleyDifficultyLevelEntity>) performTransaction(alleyDifficultyLevelManager, () -> alleyDifficultyLevelManager.getAllAlleyDifficultyLevels());
            editAlleyDto.setDifficultyLevels(this.mapToString(allAlleyDifficultyLevels));
            return Response.ok(editAlleyDto).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }


    /**
     * Pracownik edytuje szczegóły toru.
     *
     * @param editAlleyDto obiekt klasy {@link EditAlleyDto} z zedytowanymi danymi toru.
     * @return odpowiedź z kodem 200 w przypadku pomyślnie przeprowadzonej edycji danych toru,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (tor nie istniej, podane dane były błedne lub
     * nie powiodła się operacja odszyfrowania danych wersji i identyfikatora)
     */
    @PUT
    @Path("edit")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response editAlley(@Valid EditAlleyDto editAlleyDto) {
        try {
            this.performEditAlley(editAlleyDto);
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik lub klient przegląda wszystkie tory.
     *
     * @return odpowiedź z kodem 200 w przypadku istnienia aktywnych torów,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-active-alleys")
    @RolesAllowed({"ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public Response getAllActiveAlleys() {
        try {
            List<AlleyEntity> activeAlleyEntityList = (List<AlleyEntity>) performTransaction(alleyManager, () -> alleyManager.getAllActiveAlleys());
            return Response.ok(mapToAlleyDto(activeAlleyEntityList)).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik dodaje nowy tor.
     *
     * @param addAlleyDto obiekt klasy {@link AddAlleyDto} z danymi nowego toru.
     * @return odpowiedź z kodem 200 w przypadku wysłania poprawnych danych, istnienia przekazanego poziomu trudności
     * toru, nie istnienia już toru o takiej nazwie,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @POST
    @Path("add-alley")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response addAlley(@Valid AddAlleyDto addAlleyDto) {
        try {
            AlleyEntity alleyEntity = AddAlleyDto.map(addAlleyDto);
            performTransaction(alleyManager, () -> alleyManager.addAlley(alleyEntity, addAlleyDto.getAlleyDifficultyLevelName()));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik usuwa tor.
     *
     * @param alleyName nazwa usuwanego toru.
     * @return Odpowiedź z kodem 200 w przypadku zablokowania toru, odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @PUT
    @Path("remove-alley")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response removeAlley(@QueryParam("alleyName") @NotBlank
                                @Pattern(regexp = RegexPatterns.ALLEY_NAME) String alleyName) {
        try {
            performTransaction(alleyManager, () -> alleyManager.removeAlley(alleyName));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }


    /**
     * Metoda pobierająca dane toru o podanej nazwie.
     *
     * @param name nazwa toru, którego dane chcemy uzyskać.
     * @return obiekt klasy {@link EditAlleyDto} z danymi użytkownika.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private EditAlleyDto getAlleyDetailsDto(String name) throws AppException {
        AlleyEntity alleyEntity = (AlleyEntity) performTransaction(alleyManager, () -> alleyManager.getAlleyDetails(name));
        EditAlleyDto editAlleyDto = EditAlleyDto.fromAlleyEntity(alleyEntity);
        try {
            editAlleyDto.setVersion(crypt.encrypt(editAlleyDto.getVersion()));
            editAlleyDto.setId(crypt.encrypt(editAlleyDto.getId()));
            return editAlleyDto;
        } catch (EncryptException e) {
            throw new InternalProblemException();
        }
    }


    /**
     * Metoda wykonująca operacje edycji danych użytkownika.
     *
     * @param editAlleyDto obiekt klasy {@link EditAlleyDto} z zedytowanymi danymi toru.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private void performEditAlley(EditAlleyDto editAlleyDto) throws AppException {
        try {
            editAlleyDto.setVersion(crypt.decrypt(editAlleyDto.getVersion()));
            editAlleyDto.setId(crypt.decrypt(editAlleyDto.getId()));
            AlleyEntity alleyEntity = EditAlleyDto.convertToAlleyEntity(editAlleyDto);
            AlleyDifficultyLevelEntity alleyDifficultyLevelEntity = (AlleyDifficultyLevelEntity) performTransaction(alleyDifficultyLevelManager, () ->
                    alleyDifficultyLevelManager.findByName(editAlleyDto.getDifficultyLevel()));
            alleyEntity.setDifficultyLevel(alleyDifficultyLevelEntity);
            performTransaction(alleyManager, () -> alleyManager.editAlleyDetails(alleyEntity));
        } catch (DecryptException e) {
            throw new InvalidInputException(e);
        }
    }

    /**
     * Metoda konwertująca obiekty klasy {@link AlleyEntity} na obiekty klasy {@link ListAlleyDto}
     *
     * @param list lista z obiektami klasy {@link AlleyEntity}
     * @return lista z obiektami klasy {@link ListAlleyDto}
     */
    private List<ListAlleyDto> mapToAlleyDto(List<AlleyEntity> list) {
        return list
                .stream()
                .map(ListAlleyDto::map)
                .collect(Collectors.toList());

    }

    /**
     * Metoda konwertująca obiekty klasy {@link AlleyDifficultyLevelEntity} na obiekty klasy {@link String}
     *
     * @param list lista z obiektami klasy {@link AlleyDifficultyLevelEntity}
     * @return lista z obiektami klasy {@link String}
     */
    private List<String> mapToString(List<AlleyDifficultyLevelEntity> list) {
        return list
                .stream()
                .map(result -> result.getName())
                .collect(Collectors.toList());

    }
}
