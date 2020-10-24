package pl.lodz.p.it.ssbd2020.mor.endpoints;

import pl.lodz.p.it.ssbd2020.converters.LocalDateTimeConverter;
import pl.lodz.p.it.ssbd2020.entities.ReservationEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InvalidInputException;
import pl.lodz.p.it.ssbd2020.mor.endpoints.dto.*;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.ReservationManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.Crypt;
import pl.lodz.p.it.ssbd2020.utils.beanvalidation.RegexPatterns;
import pl.lodz.p.it.ssbd2020.utils.endpoint.Endpoint;
import pl.lodz.p.it.ssbd2020.utils.interceptor.MethodInvocationInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.security.enterprise.SecurityContext;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * Klasa zawierająca definicje punktów końcowych dotyczących rezerwacji.
 */

@Path("reservation")
@Interceptors(MethodInvocationInterceptor.class)
@RequestScoped
@Produces("application/json")
public class ReservationEndpoint extends Endpoint {

    /**
     * Kontekst bezpieczeństwa, który zawiera informacje o tożsamości użytkownika.
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Menedżer) odpowiedzialny za operacje związane z rezerwacjami.
     */
    @Inject
    private ReservationManagerLocal reservationManager;

    /**
     * Ziarno, które pozwala na szyfrowanie i deszyfrowanie łańcuchów znaków
     */
    @Inject
    private Crypt crypt;

    /**
     * Pracownik wyświetla listę wszystkich rezerwacji w systemie.
     *
     * @param getCanceled flaga określająca czy lista ma zwierać anulowane rezerwacjie
     * @param getPast     flaga określająca czy lsita ma zwierać rezerwacje, których data zakończenia jest w przeszłości
     * @return odpowiedź z kodem 200, lista rezerwacji w systemie lub
     * odpowiedź z kodem 400 w przypadku błedu w czasie pobierania listy
     */
    @GET
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response getAllReservations(@DefaultValue("false") @QueryParam("canceled") boolean getCanceled,
                                       @DefaultValue("false") @QueryParam("past") boolean getPast) {
        try {

            List<ReservationEntity> reservations = (List<ReservationEntity>) performTransaction(reservationManager,
                    () -> reservationManager.getAllReservations(getCanceled, getPast));
            return Response.ok(
                    reservations
                            .stream()
                            .map(ListReservationDto::fromReservationEntity)
                            .collect(Collectors.toList()))
                    .build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Klient wyświetla listę wszystkich swoich rezerwacji w systemie.
     *
     * @param getCanceled flaga określająca czy lista ma zwierać anulowane rezerwacjie
     * @param getPast     flaga określająca czy lsita ma zwierać rezerwacje, których data zakończenia jest w przeszłości
     * @return odpowiedź z kodem 200 i listą rezerwacji użytkownika, jesli udało się ją pobrać,
     * odpowiedź z kodem 400 w przeciwnym przypadku
     */
    @GET
    @Path("own")
    @RolesAllowed("ROLE_CUSTOMER")
    public Response getAllOwnReservations(@DefaultValue("false") @QueryParam("canceled") boolean getCanceled,
                                          @DefaultValue("false") @QueryParam("past") boolean getPast) {
        try {

            List<ReservationEntity> reservations = (List<ReservationEntity>) performTransaction(reservationManager,
                    () -> reservationManager.getAllCustomersReservations(
                            securityContext.getCallerPrincipal().getName(), getCanceled, getPast));
            return Response.ok(
                    reservations
                            .stream()
                            .map(ListReservationDto::fromReservationEntity)
                            .collect(Collectors.toList()))
                    .build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik wyświetla szczegóły rezerwacji.
     *
     * @param reservationNumber numer rezerwacji
     * @return odpowiedź z kodem 200 w przypadku istnienia rezerwacji o takim numerze,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-reservation")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response getReservation(@QueryParam("reservationNumber") Long reservationNumber) {
        try {
            ReservationEntity entity = (ReservationEntity) performTransaction(reservationManager, () -> reservationManager.getReservation(reservationNumber));
            ReservationDto reservationDto = ReservationDto.map(entity);
            reservationDto.setVersion(crypt.encrypt(reservationDto.getVersion()));
            reservationDto.setId(crypt.encrypt(reservationDto.getId()));
            return Response.ok(reservationDto).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Klient wyświetla szczegóły swojej rezerwacji.
     *
     * @param reservationNumber numer rezerwacji
     * @return odpowiedź z kodem 200 w przypadku istnienia rezerwacji przypisanej do użytkownika o takim numerze,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @GET
    @Path("get-own-reservation")
    @RolesAllowed("ROLE_CUSTOMER")
    public Response getOwnReservation(@QueryParam("reservationNumber") Long reservationNumber) {
        String login = securityContext.getCallerPrincipal().getName();
        try {
            ReservationEntity entity = (ReservationEntity) performTransaction(reservationManager, () -> reservationManager.getOwnReservation(login, reservationNumber));
            OwnReservationDto ownReservationDto = OwnReservationDto.map(entity);
            ownReservationDto.setVersion(crypt.encrypt(ownReservationDto.getVersion()));
            ownReservationDto.setId(crypt.encrypt(ownReservationDto.getId()));
            return Response.ok(ownReservationDto).build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik edytuje rezerwację. Po edycji, do klienta przesyłany jest email z nowymi szczegółami rezerwacji.
     *
     * @param language       język wykonawcy operacji.
     * @param reservationDto obiekt klasy {@link EditReservationDto} z zedytowanymi danymi rezerwacji.
     * @return odpowiedź z kodem 200 jeśli wybrany tor i egzemplarz broni jest dostępny w podanym czasie,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @PUT
    @Path("update-reservation")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response updateReservation(@HeaderParam("language") String language,
                                      @Valid EditReservationDto reservationDto) {
        try {
            if (reservationDto.getStartDate().getMinute() % 30 != 0 || reservationDto.getEndDate().getMinute() % 30 != 0
                    || reservationDto.getStartDate().isBefore(LocalDateTime.now())) {
                throw new InvalidInputException();
            }
            reservationDto.setVersion(crypt.decrypt(reservationDto.getVersion()));
            reservationDto.setId(crypt.decrypt(reservationDto.getId()));
            ReservationEntity reservationEntity = EditReservationDto.map(reservationDto);
            performTransaction(reservationManager, () -> reservationManager.updateReservation(reservationEntity,
                    language));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Klient edytuje własną rezerwację. Po edycji, do klienta przesyłany jest email z nowymi szczegółami rezerwacji.
     *
     * @param language       język wykonawcy operacji.
     * @param reservationDto obiekt klasy {@link EditOwnReservationDto} z edytowanymi danymi własnej rezerwacji.
     * @return odpowiedź z kodem 200 jeśli wybrany tor i model broni jest dostępny w podanym czasie,
     * odpowiedź z kodem 400 w przeciwnym przypadku.
     */
    @PUT
    @Path("update-own-reservation")
    @RolesAllowed("ROLE_CUSTOMER")
    public Response updateOwnReservation(@HeaderParam("language") String language,
                                         @Valid EditOwnReservationDto reservationDto) {
        String login = securityContext.getCallerPrincipal().getName();
        try {
            if (reservationDto.getStartDate().getMinute() % 30 != 0 || reservationDto.getEndDate().getMinute() % 30 != 0 ||
                    reservationDto.getStartDate().isBefore(LocalDateTime.now())) {
                throw new InvalidInputException();
            }

            reservationDto.setVersion(crypt.decrypt(reservationDto.getVersion()));
            reservationDto.setId(crypt.decrypt(reservationDto.getId()));
            ReservationEntity reservationEntity = EditOwnReservationDto.map(reservationDto);
            performTransaction(reservationManager,
                    () -> reservationManager.updateOwnReservation(login, reservationEntity, language));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Klient dokonuje rezerwacji po wybraniu toru, modelu broni, dnia początku rezerwacji oraz po zaznaczeniu
     * dostępnego przedziału czasowego.
     *
     * @param reservation Obiekt klasy {@link ReservationDto} reprezentujący dane rezerwacji.
     * @param language    język wykonawcy operacji.
     * @return Odpowiedź z kodem 200 jesli rezerwacja została dokonana. Odpowiedź z kodem 400 w przypadku wystąpienia błędu.
     */
    @POST
    @Path("make-reservation")
    @RolesAllowed("ROLE_CUSTOMER")
    public Response makeReservation(@HeaderParam("language") String language, @Valid AddReservationDto reservation) {
        if (language == null || language.isBlank()) {
            language = "en";
        }
        LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();
        long time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        int randomNumber = ThreadLocalRandom.current().nextInt(1000);
        String reservationNumber = time + Integer.toString(randomNumber);

        ReservationEntity reservationEntity = new ReservationEntity(Long.parseLong(reservationNumber), LocalDateTime.parse(reservation.getStartDate()), LocalDateTime.parse(reservation.getEndDate()));
        try {
//            if (reservation.getEndDate().isBefore(reservation.getStartDate())) {
//                throw new InvalidInputException();
//            }
//            if (reservation.getStartDate().getMinute() % 30 != 0 || reservation.getEndDate().getMinute() % 30 != 0) {
//                throw new InvalidInputException();
//            }
            String finalLanguage = language;
            performTransaction(reservationManager, () -> reservationManager.makeReservation(reservationEntity, reservation.getAlleyName(), reservation.getWeaponModelName(), finalLanguage));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Pracownik odwołuję rezerwację, pod warunkiem że dana rezerwacja rozpoczyna się po czasie, w którym następuje
     * próba jej usunięcia.
     *
     * @param reservationNumber numer odwoływanej rezerwacji.
     * @param language          język wykonawcy operacji.
     * @return odpowiedź z kodem 200 w przypadku pomyślnego odwołania rezerwacji,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (rezerwacja została już odwołana, rezerwacja nie istnieje)
     */
    @PUT
    @Path("cancel-reservation")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response cancelReservation(@HeaderParam("reservationNumber") @NotNull @Positive Long reservationNumber,
                                      @HeaderParam("language") String language) {
        if (language == null || language.isBlank()) {
            language = "en";
        }

        try {
            String finalLanguage = language;
            performTransaction(reservationManager, () -> reservationManager.cancelReservation(reservationNumber, finalLanguage));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }

    /**
     * Klient odwołuję rezerwację, pod warunkiem że dana rezerwacja rozpoczyna się po czasie, w którym następuje próba
     * jej usunięcia.
     *
     * @param reservationNumber numer odwoływanej rezerwacji.
     * @param language          język wykonawcy operacji.
     * @return odpowiedź z kodem 200 w przypadku pomyślnego odwołania rezerwacji,
     * odpowiedź z kodem 400 w przypadku niepowodzenia (rezerwacja została już odwołana, rezerwacja nie istnieje)
     */
    @PUT
    @Path("cancel-own-reservation")
    @RolesAllowed("ROLE_CUSTOMER")
    public Response cancelOwnReservation(@HeaderParam("reservationNumber") @NotNull @Positive Long reservationNumber,
                                         @HeaderParam("language") String language) {
        if (language == null || language.isBlank()) {
            language = "en";
        }

        try {
            String login = securityContext.getCallerPrincipal().getName();
            String finalLanguage = language;
            performTransaction(reservationManager, () -> reservationManager.cancelReservation(reservationNumber, login, finalLanguage));
            return Response.ok().build();
        } catch (AppException e) {
            return e.getResponse();
        }
    }


    /**
     * System zwraca konfliktową rezerwację w wybranym dniu na podstawie: nazwy toru, nazwy modelu broni.
     *
     * @param alleyName                 nazwa toru.
     * @param date                      dzień dla ktorego chcemy pobrac rezerwacje.
     * @param weaponModelName           nazwa modelu broni.
     * @param excludedReservationNumber numer rezerwacji, która ma zostać wykluczona z listy konfliktowych rezerwacji.
     * @return odpowiedź z kodem 200 z listą rezerwacji (może być pusta). odpowiedź z kodem 400 w przypadku wystąpienia błędu.
     */
    @GET
    @Path("get-conflict-reservations-by-weapon-model")
    @RolesAllowed("ROLE_CUSTOMER")
    public Response getConflictReservationsByWeaponModel(@QueryParam("alleyName") @NotBlank @Pattern(regexp = RegexPatterns.ALLEY_NAME)
                                                                 String alleyName,
                                                         @QueryParam("date") @NotBlank String date,
                                                         @QueryParam("weaponModelName") @Pattern(regexp = RegexPatterns.WEAPON_MODEL_NAME)
                                                                 String weaponModelName,
                                                         @QueryParam("excludedReservationNumber") Long excludedReservationNumber) {
        if (excludedReservationNumber == null) {
            excludedReservationNumber = 0L;
        }
        try {
            Long finalExcludedReservationNumber = excludedReservationNumber;
            List<ReservationEntity> conflicted = (List<ReservationEntity>) performTransaction(reservationManager,
                    () -> reservationManager.getConflictReservationsByWeaponModel(LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME),
                            alleyName, weaponModelName, finalExcludedReservationNumber));
            List<ConflictReservationDto> reservations = conflicted.stream().map(ConflictReservationDto::map).collect(Collectors.toList());
            return Response.ok(reservations).build();
        } catch (AppException e) {
            return e.getResponse();
        } catch (DateTimeParseException e) {
            return new InvalidInputException().getResponse();
        }
    }

    /**
     * System zwraca konfliktową rezerwację w wybranym dniu na podstawie: nazwy toru, numeru seryjnego broni.
     *
     * @param alleyName                 nazwa toru.
     * @param date                      dzień dla ktorego chcemy pobrać konfliktowe rezerwacje.
     * @param weaponSerialNumber        numer seryjny broni.
     * @param excludedReservationNumber numer rezerwacji, która ma zostać wykluczona z listy konfliktowych rezerwacji.
     * @return odpowiedź z kodem 200 z listą rezerwacji (może być pusta). odpowiedź z kodem 400 w przypadku wystąpienia błędu.
     */
    @GET
    @Path("get-conflict-reservations-by-weapon")
    @RolesAllowed("ROLE_EMPLOYEE")
    public Response getConflictReservationsByWeapon(@QueryParam("date") @NotBlank String date,
                                                    @QueryParam("alleyName") @NotBlank @Pattern(regexp =
                                                            RegexPatterns.ALLEY_NAME, message =
                                                            "Alley name is not valid") String alleyName,
                                                    @QueryParam("weaponSerialNumber") @NotBlank @Pattern(regexp =
                                                            RegexPatterns.WEAPON_SERIAL_NUMBER, message =
                                                            "Weapon serial number is not valid") String weaponSerialNumber,
                                                    @QueryParam("excludedReservationNumber") @NotNull @Positive long excludedReservationNumber
    ) {
        try {
            List<ConflictReservationDto> reservations =
                    (List<ConflictReservationDto>) performTransaction(reservationManager,
                            () -> reservationManager.getConflictReservationsByWeapon(LocalDateTime.parse(date,
                                    DateTimeFormatter.ISO_DATE_TIME), alleyName, weaponSerialNumber, excludedReservationNumber)
                                    .stream().map(ConflictReservationDto::map).collect(Collectors.toList()));
            return Response.ok(reservations).build();
        } catch (AppException e) {
            return e.getResponse();
        } catch (DateTimeParseException e) {
            return new InvalidInputException().getResponse();
        }
    }
}
