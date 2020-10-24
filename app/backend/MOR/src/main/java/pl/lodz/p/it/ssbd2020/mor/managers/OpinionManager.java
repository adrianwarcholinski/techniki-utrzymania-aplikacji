package pl.lodz.p.it.ssbd2020.mor.managers;

import pl.lodz.p.it.ssbd2020.entities.CustomerEntity;
import pl.lodz.p.it.ssbd2020.entities.OpinionEntity;
import pl.lodz.p.it.ssbd2020.entities.WeaponModelEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.CustomerDoesNotExistException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.AccessToAnotherCustomersOpinionException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.CustomerHasAlreadyAddedOpinionOnThisWeaponModelException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.OpinionDoesNotExistException;
import pl.lodz.p.it.ssbd2020.exceptions.mor.WeaponModelDoesNotExistException;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.CustomerFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.OpinionFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.facades.interfaces.WeaponModelFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mor.managers.interfaces.OpinionManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.interceptor.LoggingInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import javax.security.enterprise.SecurityContext;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;


/**
 * Menadżer odpowiedzialny za operacje na opiniach klientów na temat
 * modeli broni.
 */
@Stateful
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OpinionManager implements OpinionManagerLocal {

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na opiniach, z poziomem izolacji read committed.
     */
    @Inject
    private OpinionFacadeReadCommittedLocal opinionFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na modelach broni, z poziomem izolacji read committed.
     */
    @Inject
    private WeaponModelFacadeReadCommittedLocal weaponModelFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służąca do wykonywania operacji na klientach, z poziomem izolacji read committed.
     */
    @EJB(name = "MORCustomerFacadeReadCommitted")
    private CustomerFacadeReadCommittedLocal customerFacadeReadCommitted;

    /**
     * Pole reprezentujące wartość logiczna określająca, czy ostatnia transakcja w tym menadżerze została zakończona zatwierdzeniem.
     */
    private boolean isLastTransactionCommitted;

    /**
     * Pole reprezentujące identyfikator trwającej transakcji.
     */
    private String lastTransactionId;

    @Override
    public String getCurrentUser() {
        return securityContext.getCallerPrincipal() != null ? securityContext.getCallerPrincipal().getName() : "UNAUTHENTICATED";
    }

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public boolean isLastTransactionRollback() {
        return !isLastTransactionCommitted;
    }

    @Override
    public void setLastTransactionCommitted(boolean committed) {
        isLastTransactionCommitted = committed;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getLastTransactionId() {
        return lastTransactionId;
    }

    @Override
    public void setLastTransactionId(String id) {
        lastTransactionId = id;
    }

    @Override
    @RolesAllowed("addOpinion")
    public void addOpinion(OpinionEntity opinion) throws AppException {
        String login = securityContext.getCallerPrincipal().getName();
        CustomerEntity customer = customerFacadeReadCommitted.findByLogin(login)
                .orElseThrow(CustomerDoesNotExistException::new);

        WeaponModelEntity model = weaponModelFacadeReadCommitted.findByName(opinion.getWeaponModel().getName()).stream()
                .filter(WeaponModelEntity::isActive)
                .findAny()
                .orElseThrow(WeaponModelDoesNotExistException::new);

        if (model.getOpinions().stream().anyMatch(o -> o.getCustomer().equals(customer))) {
            throw new CustomerHasAlreadyAddedOpinionOnThisWeaponModelException();
        }

        long opinionNumber = opinionFacadeReadCommitted.findAll().stream()
                .mapToLong(OpinionEntity::getOpinionNumber)
                .max()
                .orElse(0);
        opinionNumber++;
        int sum = opinion.getRate() + model.getOpinions().stream().mapToInt(OpinionEntity::getRate).sum();
        double newAverageRate = sum / (float) (model.getOpinions().size() + 1);

        model.setAverageRate(newAverageRate);

        OpinionEntity newOpinion = new OpinionEntity(opinionNumber, model, customer, opinion.getContent(), opinion.getRate());

        opinionFacadeReadCommitted.create(newOpinion);
    }

    @Override
    @RolesAllowed("editOpinion")
    public void editOpinion(OpinionEntity opinion) throws AppException {
        String login = securityContext.getCallerPrincipal().getName();

        OpinionEntity currentOpinion = opinionFacadeReadCommitted.find(opinion.getId())
                .orElseThrow(OpinionDoesNotExistException::new);

        WeaponModelEntity model = currentOpinion.getWeaponModel();

        if (!model.isActive()) {
            throw new WeaponModelDoesNotExistException();
        }

        if (!currentOpinion.getCustomer().getAccount().getLogin().equals(login)) {
            throw new AccessToAnotherCustomersOpinionException();
        }

        int sum = opinion.getRate() - currentOpinion.getRate() + model.getOpinions().stream()
                .mapToInt(OpinionEntity::getRate)
                .sum();
        double newAverageRate = sum / (float) model.getOpinions().size();

        model.setAverageRate(newAverageRate);
        opinion.copyNotChangedData(currentOpinion);

        opinionFacadeReadCommitted.edit(opinion);
    }

    @Override
    @RolesAllowed("removeOpinion")
    public void removeOpinion(Long opinionNumber) throws AppException {
        OpinionEntity opinionEntity = opinionFacadeReadCommitted.findByOpinionNumber(opinionNumber).orElseThrow(OpinionDoesNotExistException::new);
        WeaponModelEntity weaponModelEntity = opinionEntity.getWeaponModel();

        if (!weaponModelEntity.isActive()) {
            throw new WeaponModelDoesNotExistException();
        }

        if (!opinionEntity.getCustomer().getAccount().getLogin().equals(securityContext.getCallerPrincipal().getName())) {
            throw new AccessToAnotherCustomersOpinionException();
        }

        opinionFacadeReadCommitted.remove(opinionEntity);
        weaponModelEntity.getOpinions().remove(opinionEntity);
        OptionalDouble average = weaponModelEntity.getOpinions().stream().mapToDouble(OpinionEntity::getRate).average();
        if (average.isPresent())
            weaponModelEntity.setAverageRate(average.getAsDouble());
        else
            weaponModelEntity.setAverageRate(null);
        weaponModelFacadeReadCommitted.edit(weaponModelEntity);
    }

    @Override
    @RolesAllowed("getAllOpinionsForWeaponModel")
    public List<OpinionEntity> getAllOpinionsForWeaponModel(@NotBlank String name) throws AppException {
        WeaponModelEntity weaponModelEntity = weaponModelFacadeReadCommitted.findByName(name).orElseThrow(WeaponModelDoesNotExistException::new);
        List<OpinionEntity> opinions = weaponModelEntity.getOpinions();
        Collections.reverse(opinions);
        return opinions;
    }

    @Override
    @RolesAllowed("getOwnOpinionForWeaponModel")
    public OpinionEntity getOwnOpinionForWeaponModel(String name) throws AppException {
        String login = securityContext.getCallerPrincipal().getName();
        return opinionFacadeReadCommitted.findByWeaponModelAndCustomerLogin(name, login)
                .orElse(null);
    }
}
