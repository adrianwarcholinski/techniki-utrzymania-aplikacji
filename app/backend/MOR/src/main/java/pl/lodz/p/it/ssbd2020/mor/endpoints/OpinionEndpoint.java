package pl.lodz.p.it.ssbd2020.mor.endpoints;

import pl.lodz.p.it.ssbd2020.entities.OpinionEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.AddOpinionDto;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.EditOpinionDto;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.ListOpinionDto;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.OpinionManagerLocal;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa zawierająca definicje punktów końcowych służących do wykonywania operacji na opiniach
 * klientów na temat modeli broni.
 */
@Path("opinion")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
@Produces("application/json")
public class OpinionEndpoint extends Endpoint {

    /**
     * Komponent EJB (Menedżer) udostępniający operacje na opiniach
     */
    @Inject
    private OpinionManagerLocal opinionManager;

    /**
     * Ziarno, które pozwala na szyfrowanie i deszyfrowanie łańcuchów znaków
     */
    @Inject
    private Crypt crypt;

    /**
     * Użytkownikowi wyświetlają się wszystkie opinie na temat wybranego modelu broni. Pojedyncza opinia zawiera
     * autora, wystawioną ocenę oraz treść opinii.
     *
     * @param weaponModelName Nazwa modelu broni dla którego chcemy pobrać opinie.
     * @return odpowiedź z kodem 200 w przypadku pobrania opinii,
     * odpowiedź z kodem 400 w przypadku braku danego modelu borni.
     */
    @GET
    @Path("get-opinions-for-weapon-model")
    @RolesAllowed("getAllOpinionsForWeaponModel")
    public Response getAllOpinionsForWeaponModel(@QueryParam("weaponModelName")
                                                 @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME) String weaponModelName) {
        try {

            List<ListOpinionDto> listOpinionDtos = (List<ListOpinionDto>) performTransaction(opinionManager,
                    () -> opinionManager.getAllOpinionsForWeaponModel(weaponModelName).stream()
                            .map(ListOpinionDto::fromOpinionEntity)
                            .collect(Collectors.toList()));

            return Response.ok(listOpinionDtos).build();
        } catch (AppException e) {
            return e.getResponse();
        }

    }

    /**
     * Klient dodaje opinię na temat dowolnego modelu broni.
     *
     * @param addOpinionDto obiekt klasy {@link AddOpinionDto} z danymi opini klienta na temat modelu broni.
     * @return odpowiedź z kodem 200 w przypadku poprawnego dodania opinii,
     * odpowiedź z kodem 400 w przypadku niepowodzenia procesu dodawania.
     */
    @POST
    @Path("add")
    @RolesAllowed("addOpinion")
    public Response addOpinion(@Valid AddOpinionDto addOpinionDto) {
        try {
            performTransaction(opinionManager, () -> opinionManager.addOpinion(addOpinionDto.map()));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Klient edytuje swoją opinię na temat dowolnego modelu broni.
     *
     * @param opinion obiekt klasy {@link EditOpinionDto} z zaktualizowanymi danymi opinii klienta na temat modelu broni.
     * @return odpowiedź z kodem 200 w przypadku poprawnej edycji opinii,
     * odpowiedź z kodem 400 w przypadku niepowodzenia procesu edycji.
     */
    @PUT
    @Path("edit")
    @RolesAllowed("editOpinion")
    public Response editOpinion(@Valid EditOpinionDto opinion) {
        try {
            opinion.setVersion(crypt.decrypt(opinion.getVersion()));
            opinion.setId(crypt.decrypt(opinion.getId()));
            performTransaction(opinionManager, () -> opinionManager.editOpinion(EditOpinionDto.map(opinion)));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Klient usuwa swoją opinię na temat wybranego modelu broni.
     *
     * @param opinionNumber numer identyfikacyjny opinii, którą chcemy usunąć
     * @return odpowiedź z kodem 200, jeśli usunięcie opinii zakończyło się powodzeniem
     * lub odpowiedź z kodem 400, jeżeli zakończyło się nie popwdeniem.
     */
    @DELETE
    @RolesAllowed("removeOpinion")
    public Response removeOpinion(@NotNull @QueryParam("number") Long opinionNumber) {
        try {
            performTransaction(opinionManager, () -> opinionManager.removeOpinion(opinionNumber));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * System zwraca użytkownikowi dane jego opinii na temat modelu broni.
     *
     * @param weaponModelName nazwa modelu broni dla którego chcemy pobrać opinie.
     * @return odpowiedź z kodem 200 w przypadku pobrania opinii,
     * odpowiedź z kodem 400 w przypadku braku danego modelu borni.
     */
    @GET
    @RolesAllowed("getOwnOpinionForWeaponModel")
    @Path("own")
    public Response getOwnOpinion(@QueryParam("name") @NotBlank @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME) String weaponModelName) {
        try {
            OpinionEntity opinion = (OpinionEntity) performTransaction(
                    opinionManager, () -> opinionManager.getOwnOpinionForWeaponModel(weaponModelName));
            if (opinion == null) {
                return Response.ok("{}").build();
            }
            String encryptedId = crypt.encrypt(Long.toString(opinion.getId()));
            String encryptedVersion = crypt.encrypt(Long.toString(opinion.getVersion()));
            return Response.ok(new EditOpinionDto(encryptedId, opinion.getOpinionNumber(),
                    opinion.getContent(), opinion.getRate(), encryptedVersion)).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }
}
