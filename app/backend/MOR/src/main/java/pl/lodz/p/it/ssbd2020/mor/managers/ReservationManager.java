package pl.lodz.p.it.ssbd2020.mor.managers;

import pl.lodz.p.it.ssbd2020.entities.*;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.CustomerDoesNotExistException;
import pl.lodz.p.it.ssbd2020.exceptions.common.SendingEmailException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.*;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.ReservationFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.WeaponModelFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.serializable.interfaces.*;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.ReservationManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.EmailCreator;
import pl.lodz.p.it.ssbd2020.utils.EmailSender;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.mail.MessagingException;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Menadżer odpowiedzialny za operacje na rezerwacjach.
 */
@Stateful
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ReservationManager implements ReservationManagerLocal {

    /**
     * Pole reprezentujące stałą określającą maksymalną długość rezerwacji w godzinach.
     */
    private int maxReservationDuration;

    /**
     * Pole reprezentujące stałą określającą godzinę otwarcia.
     */
    private int openingHour;

    /**
     * Pole reprezentujące stałą określającą godzinę zamknięcia.
     */
    private int closingHour;

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Kontekst serwletu, który pozwala odczytywać parametry z deskryptora web.xml.
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na rezerwacjach, z poziomem izolacji read committed.
     */
    @Inject
    private ReservationFacadeReadCommittedLocal reservationFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na rezerwacjach, z poziomem izolacji read committed.
     */
    @Inject
    private ReservationFacadeSerializableLocal reservationFacadeSerializable;

    /**
     * Komponent EJB (Fasada) służąca do wykonania operacji na torach, z poziomem izolacji read committed.
     */
    @Inject
    private AlleyFacadeSerializableLocal alleyFacadeSerializable;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na modelach broni, z poziomem izolacji read committed.
     */
    @Inject
    private WeaponModelFacadeReadCommittedLocal weaponModelFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na modelach broni, z poziomem izolacji read committed.
     */
    @Inject
    private WeaponModelFacadeSerializableLocal weaponModelFacadeSerializable;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na klientach, z poziomem izolacji read committed.
     **/
    @Inject
    private CustomerFacadeSerializableLocal customerFacadeSerializable;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na broniach, z poziomem izolacji read committed.
     **/
    @Inject
    private WeaponFacadeSerializableLocal weaponFacadeSerializable;

    /**
     * Ziarno, które pozwala na wysłanie wiadomości e-mail.
     */
    @Inject
    private EmailSender emailSender;

    /**
     * Ziarno, które pozwala na wygenerowanie wiadomości email.
     */
    @Inject
    private EmailCreator emailCreator;

    /**
     * Pole reprezentujące wartość logiczna określająca, czy ostatnia transakcja w tym menadżerze została zakończona zatwierdzeniem.
     */
    private boolean isLastTransactionCommitted;

    /**
     * Pole reprezentujące identyfikator trwającej transakcji.
     */
    private String lastTransactionId;

    @PostConstruct
    public void init() {
        maxReservationDuration = Integer.parseInt(servletContext.getInitParameter("MAX_RESERVATION_DURATION"));
        openingHour = Integer.parseInt(servletContext.getInitParameter("OPENING_HOUR"));
        closingHour = Integer.parseInt(servletContext.getInitParameter("CLOSING_HOUR"));
    }

    @Override
    public String getCurrentUser() {
        return securityContext.getCallerPrincipal() != null ? securityContext.getCallerPrincipal().getName() : "UNAUTHENTICATED";
    }

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public boolean isLastTransactionRollback() {
        return !isLastTransactionCommitted;
    }

    @Override
    public void setLastTransactionCommitted(boolean committed) {
        isLastTransactionCommitted = committed;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public String getLastTransactionId() {
        return lastTransactionId;
    }

    @Override
    public void setLastTransactionId(String id) {
        lastTransactionId = id;
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public List<ReservationEntity> getAllReservations(boolean getCanceled, boolean getPast) throws AppException {
        return reservationFacadeReadCommitted.findAll(getCanceled, getPast);
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN","ROLE_CUSTOMER"})
    public List<ReservationEntity> getAllCustomersReservations(String login, boolean getCanceled, boolean getPast) throws AppException {
        return reservationFacadeReadCommitted.findByCustomer(login, getCanceled, getPast);
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public ReservationEntity getReservation(Long reservationNumber) throws AppException {
        return reservationFacadeReadCommitted.findByReservationNumber(reservationNumber).orElseThrow(ReservationDoesNotExistException::new);
    }

    @Override
    @RolesAllowed("ROLE_CUSTOMER")
    public ReservationEntity getOwnReservation(String login, Long reservationNumber) throws AppException {
        ReservationEntity reservationEntity = reservationFacadeReadCommitted.findByReservationNumber(reservationNumber).orElseThrow(ReservationDoesNotExistException::new);
        if (reservationEntity.getCustomer().getAccount().getLogin().equals(login)) {
            return reservationEntity;
        } else {
            throw new ReservationDoesNotBelongToTheUserException();
        }
    }

    @Override
    @RolesAllowed("ROLE_CUSTOMER")
    public void updateOwnReservation(String login, ReservationEntity targetEntity, String language) throws AppException {
        ReservationEntity originalEntity =
                reservationFacadeSerializable.find(targetEntity.getId())
                        .orElseThrow(ReservationDoesNotExistException::new);
        if (!originalEntity.getCustomer().getAccount().getLogin().equals(login)) {
            throw new ReservationDoesNotBelongToTheUserException();
        }
        checkReservationEditability(originalEntity);

        checkReservationDuration(targetEntity.getStartDate(), targetEntity.getEndDate());
        checkOpeningHours(targetEntity.getStartDate(), targetEntity.getEndDate());

        AlleyEntity alleyEntity = getActiveAlleyByName(targetEntity.getAlley().getName());

        WeaponModelEntity weaponModel =
                weaponModelFacadeSerializable.findByName(targetEntity.getWeapon().getWeaponModel().getName())
                        .orElseThrow(WeaponModelDoesNotExistException::new);

        if(!weaponModel.isActive()){
            throw new WeaponModelIsAlreadyDeactivatedException();
        }

        List<WeaponEntity> activeWeapons =
                weaponModel.getWeapons().stream().filter(WeaponEntity::isActive).collect(Collectors.toList());

        List<ReservationEntity> conflictedReservations = reservationFacadeSerializable.findConflictReservationsByWeaponModel(targetEntity.getStartDate(),
                targetEntity.getEndDate(), weaponModel.getName(), alleyEntity.getName())
                .stream().filter(reservationEntity -> !reservationEntity.equals(originalEntity)).collect(Collectors.toList());

        if (conflictedReservations.stream().anyMatch((reservationEntity -> reservationEntity.getAlley().equals(alleyEntity)))) {
            throw new AlleyIsNotAvailableException();
        }

        Set<String> unavailableWeaponSerialNumbers =
                conflictedReservations.stream().map(reservationEntity -> reservationEntity.getWeapon()
                        .getSerialNumber()).collect(Collectors.toSet());
        Set<WeaponEntity> availableWeapons = activeWeapons.stream()
                .filter(weaponEntity -> !unavailableWeaponSerialNumbers.contains(weaponEntity.getSerialNumber()))
                .collect(Collectors.toSet());
        if (availableWeapons.isEmpty()) {
            throw new WeaponModelIsNotAvailableException();
        }
        targetEntity.copyNotChangedReservationData(originalEntity);
        targetEntity.setWeapon(availableWeapons.iterator().next());
        targetEntity.setAlley(alleyEntity);
        reservationFacadeSerializable.edit(targetEntity);
        try {
            emailSender.sendEmail(emailCreator.getEmailForReservationEdit(language,
                    targetEntity.getCustomer().getAccount().getEmail(),
                    false,
                    String.valueOf(targetEntity.getReservationNumber()),
                    targetEntity.getWeapon().getWeaponModel().getName(),
                    targetEntity.getAlley().getName(),
                    targetEntity.getStartDate(),
                    targetEntity.getEndDate()));
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public void updateReservation(ReservationEntity targetEntity, String language) throws AppException {
        ReservationEntity originalEntity =
                reservationFacadeSerializable.find(targetEntity.getId())
                        .orElseThrow(ReservationDoesNotExistException::new);

        checkReservationEditability(originalEntity);

        checkReservationDuration(targetEntity.getStartDate(), targetEntity.getEndDate());
        checkOpeningHours(targetEntity.getStartDate(), targetEntity.getEndDate());

        AlleyEntity alleyEntity = getActiveAlleyByName(targetEntity.getAlley().getName());

        WeaponEntity weaponEntity = getActiveWeaponBySerialNumber(targetEntity.getWeapon().getSerialNumber());

        List<ReservationEntity> conflictedReservations = reservationFacadeSerializable.findConflictReservationsByWeapon(targetEntity.getStartDate(),
                targetEntity.getEndDate(), weaponEntity.getSerialNumber(), alleyEntity.getName())
                .stream().filter(reservationEntity -> !reservationEntity.equals(originalEntity)).collect(Collectors.toList());

        if (conflictedReservations.stream().anyMatch((reservationEntity -> reservationEntity.getAlley().equals(alleyEntity)))) {
            throw new AlleyIsNotAvailableException();
        }

        if (conflictedReservations.stream().anyMatch((reservationEntity -> reservationEntity.getWeapon().equals(weaponEntity)))) {
            throw new WeaponIsNotAvailableException();
        }
        targetEntity.copyNotChangedReservationData(originalEntity);
        targetEntity.setWeapon(weaponEntity);
        targetEntity.setAlley(alleyEntity);
        reservationFacadeSerializable.edit(targetEntity);
        try {
            emailSender.sendEmail(emailCreator.getEmailForReservationEdit(language,
                    targetEntity.getCustomer().getAccount().getEmail(),
                    true,
                    String.valueOf(targetEntity.getReservationNumber()),
                    targetEntity.getWeapon().getWeaponModel().getName(),
                    targetEntity.getAlley().getName(),
                    targetEntity.getStartDate(),
                    targetEntity.getEndDate()));
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    @Override
    @RolesAllowed("ROLE_CUSTOMER")
    public void makeReservation(ReservationEntity entity, String alleyName, String weaponModelName, String language) throws AppException {
        CustomerEntity customerEntity = customerFacadeSerializable.findByLogin(getCurrentUser()).orElseThrow(CustomerDoesNotExistException::new);
        AlleyEntity alleyEntity = getActiveAlleyByName(alleyName);
        WeaponModelEntity weaponModelEntity = weaponModelFacadeSerializable.findByName(weaponModelName).orElseThrow(WeaponModelDoesNotExistException::new);

        if (!weaponModelEntity.isActive()) {
            throw new WeaponModelIsAlreadyDeactivatedException();
        }

        checkReservationDuration(entity.getStartDate(), entity.getEndDate());
        checkOpeningHours(entity.getStartDate(), entity.getEndDate());

        List<ReservationEntity> conflictedReservation = reservationFacadeSerializable.findConflictReservationsByWeaponModel(entity.getStartDate(), entity.getEndDate(), weaponModelName, alleyName);
        checkIfAlleyIsAvailable(conflictedReservation, alleyEntity);

        WeaponEntity weaponToReserve = getActiveWeaponByWeaponModel(conflictedReservation, weaponModelEntity);

        weaponToReserve = weaponFacadeSerializable.findBySerialNumber(weaponToReserve.getSerialNumber()).orElseThrow(WeaponModelDoesNotExistException::new);
        if(!weaponToReserve.isActive()){
            throw new WeaponIsAlreadyDeactivatedException();
        }
        addReservation(entity, customerEntity, alleyEntity, weaponToReserve);
        try {
            emailSender.sendEmail(emailCreator.getMakeReservationEmail(language, customerEntity.getAccount().getEmail(),
                    Long.toString(entity.getReservationNumber()),
                    weaponModelName,
                    alleyName,
                    entity.getStartDate(),
                    entity.getEndDate()
            ));
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    /**
     * Metoda pomocnicza służącą do dodania rezerwacji.
     * @param entity dodawana rezerwacja
     * @param customerEntity rezerwujący klient
     * @param alleyEntity rezerwowany tor
     * @param weaponToReserve rezerwowany egzemplarz broni
     * @throws AppException gdy operacja zakończy się niepowodzeniem.
     */
    private void addReservation(ReservationEntity entity, CustomerEntity customerEntity, AlleyEntity alleyEntity, WeaponEntity weaponToReserve) throws AppException {
        entity.setActive(true);
        entity.setAlley(alleyEntity);
        entity.setWeapon(weaponToReserve);
        entity.setCustomer(customerEntity);
        weaponToReserve.getReservations().add(entity);
        alleyEntity.getReservations().add(entity);
        reservationFacadeSerializable.create(entity);
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public void cancelReservation(long reservationNumber, String language) throws AppException {
        ReservationEntity entity = reservationFacadeReadCommitted.findByReservationNumber(reservationNumber).orElseThrow(ReservationDoesNotExistException::new);
        this.cancelReservation(entity);

        CustomerEntity customerEntity = entity.getCustomer();
        try {
            emailSender.sendEmail(emailCreator.getCancelReservationEmail(language, customerEntity.getAccount().getEmail(), Long.toString(entity.getReservationNumber())));
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    @Override
    @RolesAllowed("ROLE_CUSTOMER")
    public void cancelReservation(long reservationNumber, String login, String language) throws AppException {
        ReservationEntity entity = reservationFacadeReadCommitted.findByReservationNumber(reservationNumber).orElseThrow(ReservationDoesNotExistException::new);
        if (!entity.getCustomer().getAccount().getLogin().equals(login)) {
            throw new ReservationDoesNotBelongToTheUserException();
        }
        this.cancelReservation(entity);

        CustomerEntity customerEntity = entity.getCustomer();
        try {
            emailSender.sendEmail(emailCreator.getCancelOwnReservationEmail(language, customerEntity.getAccount().getEmail(), Long.toString(entity.getReservationNumber())));
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    @Override
    @RolesAllowed("ROLE_CUSTOMER")
    public List<ReservationEntity> getConflictReservationsByWeaponModel(LocalDateTime date,
                                                                        String alleyName,
                                                                        String weaponModelName,
                                                                        Long excludedReservationNumber) throws AppException {
        LocalDateTime dateToCheck = date.withHour(openingHour);
        LocalDateTime end = date.withHour(closingHour);

        List<ReservationEntity> reservationsToCheck = reservationFacadeReadCommitted.findConflictReservationsByWeaponModel(dateToCheck, end, weaponModelName, alleyName)
                .stream()
                .filter(reservationEntity -> reservationEntity.getReservationNumber() != excludedReservationNumber)
                .collect(Collectors.toList());

        List<ReservationEntity> conflicted = reservationsToCheck.stream()
                .filter(reservationEntity -> reservationEntity.getAlley().getName().equals(alleyName))
                .collect(Collectors.toList());

        int amountOfWeapons = (int) weaponModelFacadeReadCommitted.findByName(weaponModelName)
                .orElseThrow(WeaponModelDoesNotExistException::new)
                .getWeapons()
                .stream()
                .filter(WeaponEntity::isActive).count();


        while (!dateToCheck.isEqual(end)) {
            int weaponModelCounter = 0;
            for (ReservationEntity reservationEntity : reservationsToCheck) {
                LocalDateTime reservationStartDate = reservationEntity.getStartDate();
                LocalDateTime reservationEndDate = reservationEntity.getEndDate();
                String reservationWeaponModelName = reservationEntity.getWeapon().getWeaponModel().getName();
                if (!conflicted.contains(reservationEntity)) {
                    if (reservationStartDate.isEqual(dateToCheck) || reservationStartDate.isBefore(dateToCheck) && reservationEndDate.isAfter(dateToCheck)) {
                        if (reservationWeaponModelName.equals(weaponModelName)) {
                            weaponModelCounter++;
                        }
                    }
                }
            }
            if (weaponModelCounter == amountOfWeapons) {
                conflicted.add(new ReservationEntity(dateToCheck, dateToCheck.plusMinutes(30)));
            }
            dateToCheck = dateToCheck.plusMinutes(30);
        }

        return conflicted;
    }

    @Override
    @RolesAllowed("ROLE_EMPLOYEE")
    public List<ReservationEntity> getConflictReservationsByWeapon(LocalDateTime date,
                                                                   String alleyName,
                                                                   String weaponSerialNumber,
                                                                   long excludedReservationNumber) throws AppException {
        return reservationFacadeReadCommitted.findConflictReservationsByWeapon(
                date.withHour(Integer.parseInt(servletContext.getInitParameter("OPENING_HOUR"))),
                date.withHour(Integer.parseInt(servletContext.getInitParameter("CLOSING_HOUR"))),
                weaponSerialNumber,
                alleyName)
                .stream().filter(reservationEntity -> reservationEntity.getReservationNumber() != excludedReservationNumber)
                .collect(Collectors.toList());
    }


    /**
     * Metoda sprawdzająca czy rezerwacja nie przekracza maksymalnego dopuszczalnego czasu.
     *
     * @param startDate początek rezerwacji.
     * @param endDate   koniec rezerwacji.
     * @throws AppException jeśli rezerwacja przekracza dopuszczalny czas.
     */
    private void checkReservationDuration(LocalDateTime startDate, LocalDateTime endDate) throws AppException {
        Duration duration = Duration.between(startDate, endDate);
        if (duration.toMinutes() > maxReservationDuration * 60) {
            throw new ReservationIsTooLongException();
        }
    }

    /**
     * Metoda sprawdzająca czy czas rezerwacji znajduję się w godzinach otwarcia strzelnicy.
     *
     * @param startDate początek rezerwacji
     * @param endDate   koniec rezerwacji
     * @throws AppException jeśli rezerwacja nie znajduję się w godzinach otwarcia strzelnicy.
     */
    private void checkOpeningHours(LocalDateTime startDate, LocalDateTime endDate) throws AppException {
        if (startDate.getHour() < openingHour || endDate.getHour() < openingHour) {
            throw new ReservationDuringClosingHoursException();
        } else if (startDate.getHour() > closingHour || endDate.getHour() > closingHour) {
            throw new ReservationDuringClosingHoursException();
        } else if (endDate.getHour() == closingHour && endDate.getMinute() != 0) {
            throw new ReservationDuringClosingHoursException();
        }
    }

    /**
     * Metoda sprawdzająca czy tor jest dostępny w konfliktowych rezerwacjach
     *
     * @param conflictedReservations lista konfliktowych rezerwacji
     * @param alleyEntity            tor do sprawdzenia
     * @throws AppException jeśli tor nie jest dostępny.
     */
    private void checkIfAlleyIsAvailable(List<ReservationEntity> conflictedReservations, AlleyEntity alleyEntity) throws AppException {

        if (conflictedReservations.stream().anyMatch((reservationEntity -> reservationEntity.getAlley().equals(alleyEntity)))) {
            throw new AlleyIsNotAvailableException();
        }
    }

    /**
     * Metoda odwołująca rezerwację.
     *
     * @param entity encja reprezentująca odwoływaną rezerwację
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private void cancelReservation(ReservationEntity entity) throws AppException {
        if (entity.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ReservationIsAlreadyFinishedException();
        }
        if (entity.getStartDate().isBefore(LocalDateTime.now()) && entity.getEndDate().isAfter(LocalDateTime.now())) {
            throw new ReservationInProgressException();
        }
        if (!entity.isActive()) {
            throw new ReservationIsAlreadyCanceledException();
        }
        entity.setActive(false);
        reservationFacadeReadCommitted.edit(entity);
    }

    /**
     * Metoda sprawdzająca czy rezerwacja pobrana z bazy danych jest edytowalna.
     *
     * @param originalEntity encja pobrana z bazy danych.
     * @throws AppException jeśli rezerwacja nie może zostać zeedytowana.
     */
    private void checkReservationEditability(ReservationEntity originalEntity) throws AppException {
        if (!originalEntity.isActive()) {
            throw new ReservationIsAlreadyCanceledException();
        }
        if (originalEntity.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ReservationIsAlreadyFinishedException();
        }
        if (originalEntity.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ReservationInProgressException();
        }
    }

    /**
     * Metoda zwracająca aktywny egzemplarz broni na podstawie numeru seryjnego.
     *
     * @param serialNumber numer seryjny broni.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private WeaponEntity getActiveWeaponBySerialNumber(String serialNumber) throws AppException {
        WeaponEntity weaponEntity = weaponFacadeSerializable.findBySerialNumber(serialNumber)
                .orElseThrow(WeaponDoesNotExistException::new);
        if (!weaponEntity.isActive()) {
            throw new WeaponIsAlreadyDeactivatedException();
        }
        return weaponEntity;
    }

    /**
     * Metoda zwracająca egzemplarz borni do zarezerwowania.
     *
     * @param conflictedReservations lista konfliktowych rezerwacji.
     * @param weaponModel            model bronim który ma być zarezerwowany.
     * @return egzemplarz do rezerwacji.
     * @throws AppException jeśli brakuje dostępnego egzemplarza broni.
     */
    private WeaponEntity getActiveWeaponByWeaponModel(List<ReservationEntity> conflictedReservations, WeaponModelEntity weaponModel) throws AppException {
        List<WeaponEntity> activeWeapons =
                weaponModel.getWeapons().stream().filter(WeaponEntity::isActive).collect(Collectors.toList());
        Set<String> unavailableWeaponSerialNumbers =
                conflictedReservations.stream().map(reservationEntity -> reservationEntity.getWeapon()
                        .getSerialNumber()).collect(Collectors.toSet());
        Set<WeaponEntity> availableWeapons = activeWeapons.stream()
                .filter(weaponEntity -> !unavailableWeaponSerialNumbers.contains(weaponEntity.getSerialNumber()))
                .collect(Collectors.toSet());
        if (availableWeapons.isEmpty()) {
            throw new WeaponModelIsNotAvailableException();
        }
        return availableWeapons.iterator().next();
    }

    /**
     * Metoda zwracająca aktywny tor na podstawie nazwy.
     *
     * @param alleyName nazwa toru.
     * @return obiekt zgodny z podanym parametrem.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private AlleyEntity getActiveAlleyByName(String alleyName) throws AppException {
        AlleyEntity alleyEntity = alleyFacadeSerializable.findByName(alleyName).orElseThrow(AlleyDoesNotExistException::new);
        if (!alleyEntity.isActive()) {
            throw new AlleyIsAlreadyDeactivatedException();
        }
        return alleyEntity;
    }
}
