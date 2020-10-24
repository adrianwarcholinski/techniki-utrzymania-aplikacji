package pl.lodz.p.it.ssbd2020.mok.managers;

import pl.lodz.p.it.ssbd2020.entities.*;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.exceptions.common.AccountDoesNotExistException;
import pl.lodz.p.it.ssbd2020.exceptions.common.InternalProblemException;
import pl.lodz.p.it.ssbd2020.exceptions.common.SendingEmailException;
import pl.lodz.p.it.ssbd2020.exceptions.mok.*;
import pl.lodz.p.it.ssbd2020.mok.facades.interfaces.*;
import pl.lodz.p.it.ssbd2020.mok.managers.interfaces.AccountManagerLocal;
import pl.lodz.p.it.ssbd2020.utils.*;
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
import javax.mail.MessagingException;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Menadżer odpowiedzialny za operacje na kontach użytkowników.
 */
@Stateful
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountManager implements AccountManagerLocal {

    /**
     * Kontekst, który pozwala na odczytanie parametrów z deskryptora web.xml
     */
    @Inject
    private ServletContext servletContext;

    /**
     * Kontekst bezpieczeństwa, który zwraca informacje na temat zalogowanego użytkownika
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Komponent EJB (Fasada) służący do wykonywania operacji na kontach, z poziomem izolacji readCommitted.
     **/
    @Inject
    private AccountFacadeReadCommittedLocal accountFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służący do wykonywania operacji na kontach administratorów, z poziomem izolacji readCommitted.
     **/
    @Inject
    private AdminFacadeReadCommittedLocal adminFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służący do wykonywania operacji na kontach pracowników, z poziomem izolacji readCommitted.
     **/
    @Inject
    private EmployeeFacadeReadCommittedLocal employeeFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służący do wykonywania operacji na kontach klientów, z poziomem izolacji readCommitted.
     **/
    @EJB(name = "MOKCustomerFacadeReadCommitted")
    private CustomerFacadeReadCommittedLocal customerFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służący do dodawania i pobierania zużytych tokenów, z poziomem izolacji readCommitted.
     **/
    @Inject
    private ExpiredTokenFacadeReadCommittedLocal expiredTokenFacadeReadCommitted;

    /**
     * Komponent EJB (Fasada) służący do wykonywania operacji na poziomach dostępu, z poziomem izolacji readCommitted.
     **/
    @Inject
    private AccessLevelFacadeReadCommittedLocal accessLevelFacadeReadCommitted;

    /**
     * Ziarno, które pozwala na wysłanie wiadomości e-mail.
     */
    @Inject
    private EmailSender emailSender;

    /**
     * Ziarno, które pozwala na szyfrowanie i deszyfrowanie łańcuchów znaków
     */
    @Inject
    private Crypt crypt;

    /**
     * Ziarno, które pozwala na generowanie hasha hasła oraz jego weryfikacje.
     */
    @Inject
    private HashGenerator hashGenerator;

    /**
     * Ziarno, które pozwala na wygenerowanie wiadomości email.
     */
    @Inject
    private EmailCreator emailCreator;

    /**
     * Wartość logiczna określająca, czy ostatnia transakcja w tym menadżerze została zakończona zatwierdzeniem.
     */
    private boolean isLastTransactionCommitted;

    /**
     * Identyfikator trwającej transakcji.
     */
    private String lastTransactionId;

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
    public String getCurrentUser() {
        return securityContext.getCallerPrincipal() != null ? securityContext.getCallerPrincipal().getName() : "UNAUTHENTICATED";
    }

    @Override
    @PermitAll
    public void addAccount(AccountEntity account, String language) throws AppException {
        String hashedPassword = hashGenerator.generatePasswordHash(account.getPassword());
        if (accountFacadeReadCommitted.findByEmail(account.getEmail()).isPresent())
            throw new EmailIsAlreadyTakenException();
        if (accountFacadeReadCommitted.findByLogin(account.getLogin()).isPresent())
            throw new LoginIsAlreadyTakenException();
        account.setPassword(hashedPassword);
        this.checkGrantIsAvailable(account.getAccessLevels().get(0), account.getLogin());
        accountFacadeReadCommitted.create(account);

        try {
            emailSender.sendEmail(emailCreator.getVerificationEmail(language, account.getEmail(), LocalDateTime.now().toString(), account.getLogin()));
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        } catch (EncryptException e) {
            throw new InternalProblemException(e);
        }
    }

    @Override
    @PermitAll
    public void verify(String login, String language) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);

        if (account.isVerified()) {
            throw new AccountIsAlreadyVerifiedException();
        }

        account.setVerified(true);
        accountFacadeReadCommitted.edit(account);
        try {
            emailSender.sendEmail(emailCreator.getVerificationConfirmEmail(language, account.getEmail(), account.getLogin()));
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public void changeOwnPassword(String login, String oldPassword, String newPassword) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);

        String currentPasswordHash = account.getPassword();
        if (hashGenerator.verifyPassword(oldPassword, currentPasswordHash)) {
            String newPasswordHash = hashGenerator.generatePasswordHash(newPassword);
            account.setPassword(newPasswordHash);
        } else {
            throw new InvalidOldPasswordException();
        }

        accountFacadeReadCommitted.edit(account);
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public void changePassword(String login, String newPassword) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);

        account.setPassword(hashGenerator.generatePasswordHash(newPassword));
        accountFacadeReadCommitted.edit(account);
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN"})
    public void grantAccessLevel(String accessLevel, String login, String info) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);
        checkGrantIsAvailable(accessLevel, login, info);
        Optional<AccessLevelEntity> accessLevelEntity = accessLevelFacadeReadCommitted.findByAccessLevelAndLogin(accessLevel, login);
        if (accessLevelEntity.isPresent()) {
            grantAccessLevel(accessLevelEntity.get(), info);
        } else {
            grantAccessLevel(accessLevel, account, info);
        }
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public void revokeAccessLevel(String login, String accessLevel) throws AppException {
        AccessLevelEntity accessLevelEntity =
                accessLevelFacadeReadCommitted.findByAccessLevelAndLogin(accessLevel, login).orElseThrow(GrantAccessLevelInvalidRoleException::new);
        Long accountRolesNumber = accessLevelFacadeReadCommitted.countAccountRoles(login)
                .orElseThrow(AccountDoesNotExistException::new);
        if (!accessLevelEntity.isActive()) {
            throw new AccessLevelAlreadyRevokedException();
        } else if (accountRolesNumber == 1) {
            throw new RevokeLastAccessLevelException();
        } else if (adminFacadeReadCommitted.countActive(true) == 1) {
            throw new RevokeLastAdminAccessLevelException();
        }
        accessLevelEntity.setActive(false);
        accessLevelFacadeReadCommitted.edit(accessLevelEntity);
    }

    @Override
//    @RolesAllowed({"getAccountDetails", "getOwnAccountDetails"})
    @PermitAll
    public AccountEntity getAccountDetails(String login) throws AppException {
        return accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);
    }

    @Override
    @PermitAll
    public void sendEmailForResetPassword(String email, String browserLanguage) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByEmail(email).orElseThrow(AccountDoesNotExistException::new);
        String login = account.getLogin();
        int tokenLifeTimeMins = Integer.parseInt(servletContext.getInitParameter("RESET_PASSWORD_LINK_LIFETIME"));
        String tokenLifeTime = LocalDateTime.now().plusMinutes(tokenLifeTimeMins).toString();

        try {
            emailSender.sendEmail(emailCreator.getResetPasswordEmail(browserLanguage, email, login, tokenLifeTime));
        } catch (EncryptException e) {
            throw new InternalProblemException(e);
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    @Override
    @PermitAll
    public void resetPassword(String login, String newPassword) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);
        String newPasswordHash = hashGenerator.generatePasswordHash(newPassword);
        account.setPassword(newPasswordHash);
        accountFacadeReadCommitted.edit(account);
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN"})
    public List<AccountEntity> getAllAccounts() throws AppException {
        return accountFacadeReadCommitted.getAllSortedById();
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public void editUserDetails(AccountEntity accountEntity, AdminEntity adminEntity, CustomerEntity customerEntity,
                                EmployeeEntity employeeEntity) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.find(accountEntity.getId()).orElseThrow(AccountDoesNotExistException::new);
        accountEntity.copyNotChangedDataAccount(account);

        if (adminEntity != null) {
            Optional<AdminEntity> ownerAdminEntity = adminFacadeReadCommitted.findByCardNumber(adminEntity.getCardNumber());
            if (!this.checkUniqueCardNumber(ownerAdminEntity, accountEntity.getLogin())) {
                throw new CardNumberIsAlreadyTakenException();
            }
        }

        if (customerEntity != null) {
            Optional<CustomerEntity> ownerCustomerEntity = customerFacadeReadCommitted.findByPhoneNumber(customerEntity.getPhoneNumber());
            if (!this.checkUniquePhoneNumber(ownerCustomerEntity, accountEntity.getLogin())) {
                throw new PhoneNumberIsAlreadyTakenException();
            }
        }

        if (employeeEntity != null) {
            Optional<EmployeeEntity> ownerEmployeeEntity = employeeFacadeReadCommitted.findByWorkPhoneNumber(employeeEntity.getWorkPhoneNumber());
            if (!this.checkUniqueWorkPhoneNumber(ownerEmployeeEntity, accountEntity.getLogin())) {
                throw new WorkPhoneNumberIsAlreadyTakenException();
            }
        }

        accountFacadeReadCommitted.edit(accountEntity);

        for (AccessLevelEntity ent : account.getAccessLevels()) {
            if (adminEntity != null && ent instanceof AdminEntity) {
                adminEntity.copyNotChangedDataAccessLevel(ent);
                adminFacadeReadCommitted.edit(adminEntity);
            } else if (customerEntity != null && ent instanceof CustomerEntity) {
                customerEntity.copyNotChangedDataAccessLevel(ent);
                customerFacadeReadCommitted.edit(customerEntity);
            } else if (employeeEntity != null && ent instanceof EmployeeEntity) {
                employeeEntity.copyNotChangedDataAccessLevel(ent);
                employeeFacadeReadCommitted.edit(employeeEntity);
            }
        }
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public void changeEmail(String cipherText) throws AppException {
        String[] decrypted;
        if (expiredTokenFacadeReadCommitted.findByToken(cipherText).isPresent()) {
            throw new ExpiredTokenException();
        }
        try {
            decrypted = crypt.decrypt(cipherText).split(";");
        } catch (DecryptException e) {
            throw new LinkCorruptedException(e);
        }
        if (decrypted.length != 3) {
            throw new LinkCorruptedException();
        }
        String login = decrypted[0];
        String email = decrypted[1];
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);

        if (accountFacadeReadCommitted.findByEmail(email).isPresent())
            throw new EmailIsAlreadyTakenException();

        account.setEmail(email);
        accountFacadeReadCommitted.edit(account);
        expiredTokenFacadeReadCommitted.create(new ExpiredTokenEntity(cipherText));
    }


    @Override
    @RolesAllowed("ROLE_ADMIN")
    public void lockAccount(String login) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);

        if (!account.isVerified()) {
            throw new AccountIsNotVerifiedException();
        }
        if (!account.isActive()) {
            throw new AccountIsAlreadyLockedException();
        }
        checkBlockLastAdminAttempt(account);
        account.setActive(false);
        accountFacadeReadCommitted.edit(account);
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public void unlockAccount(String login) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);

        if (!account.isVerified()) {
            throw new AccountIsNotVerifiedException();
        }
        if (account.isActive()) {
            throw new AccountIsAlreadyUnlockedException();
        }
        account.setActive(true);
        accountFacadeReadCommitted.edit(account);
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public boolean isVerifiedAccountWithEmail(String email) throws AppException {
        return accountFacadeReadCommitted.findByEmail(email).map(AccountEntity::isVerified).orElse(false);
    }

    @Override
    @PermitAll
    public void handleUnsuccessfulAuthenticationAttempt(String login) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);
        account.setLastUnsuccessfulAuthentication(LocalDateTime.now());
        account.setUnsuccessfulAuthenticationCount(account.getUnsuccessfulAuthenticationCount() + 1);
        accountFacadeReadCommitted.edit(account);
    }

    @Override
    @PermitAll
    public boolean lockAccountAfterFailedAuthentication(String login, String language) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);
        if (account.getUnsuccessfulAuthenticationCount() >= 3 && account.isActive()) {
            checkBlockLastAdminAttempt(account);
            account.setActive(false);
            account.setUnsuccessfulAuthenticationCount(0);
            accountFacadeReadCommitted.edit(account);
            try {
                emailSender.sendEmail(emailCreator.getLockAccountEmail(language, account.getEmail()));
            } catch (MessagingException e) {
                throw new SendingEmailException(e);
            }
            return true;
        } else
            return false;
    }

    @Override
    @PermitAll
    public void handleSuccessfulAuthentication(String login, String ip, String lang, boolean isAdmin) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);
        account.setLastSuccessfulAuthentication(LocalDateTime.now());
        account.setUnsuccessfulAuthenticationCount(0);
        account.setLastUsedIpAddress(ip);
        accountFacadeReadCommitted.edit(account);
        if (isAdmin) {
            try {
                emailSender.sendEmail(emailCreator.getAdminAuthenticationEmail(lang, account.getEmail(), ip));
            } catch (MessagingException e) {
                throw new SendingEmailException(e);
            }
        }
    }

    @Override
    @PermitAll
    public LastAuthenticationTimes getLastAuthenticationTimes(String login) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);
        return new LastAuthenticationTimes(account.getLastUnsuccessfulAuthentication(), account.getLastSuccessfulAuthentication());
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public List<AccountEntity> findByPhraseInFullName(String phrase) throws AppException {
        return accountFacadeReadCommitted.findByPhraseInFullName(phrase);
    }

    @Override
    @RolesAllowed({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    public void sendEmailForChangeEmail(String login, String newEmail, String lang, boolean byAdmin) throws AppException {
        AccountEntity account = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);

        if (account.getEmail().equals(newEmail)) {
            throw new NewEmailSameAsCurrentEmailException();
        }
        if (accountFacadeReadCommitted.findByEmail(newEmail).isPresent())
            throw new EmailIsAlreadyTakenException();
        try {
            emailSender.sendEmail(emailCreator.getEmailForChangeEmail(lang, newEmail, byAdmin, login, newEmail, LocalDateTime.now().toString()));
        } catch (EncryptException e) {
            throw new InternalProblemException(e);
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public List<AccountEntity> getAuthenticatedAccounts() throws AppException {
        return accountFacadeReadCommitted.findAuthenticated();
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public void sendVerificationLink(String login, String language) throws AppException {
        AccountEntity accountEntity = accountFacadeReadCommitted.findByLogin(login).orElseThrow(AccountDoesNotExistException::new);
        if (accountEntity.isVerified()) {
            throw new AccountIsAlreadyVerifiedException();
        }
        try {
            emailSender.sendEmail(emailCreator.getVerificationEmail(language, accountEntity.getEmail(), LocalDateTime.now().toString(), accountEntity.getLogin()));
        } catch (MessagingException e) {
            throw new SendingEmailException(e);
        }
    }

    /**
     * Metoda sprawdzająca, czy występuje próba zablokowania ostatniego aktywnego administratora systemu.
     *
     * @param account Konto użytkownika.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private void checkBlockLastAdminAttempt(AccountEntity account) throws AppException {
        if (adminFacadeReadCommitted.countActive(true) == 1
                && account.getAccessLevels().stream().anyMatch(level -> level.isActive() && level instanceof AdminEntity)) {
            throw new AttemptToLockLastAdminAccountException();
        }
    }

    /**
     * Metoda tworząca i dodająca poziom dostępu.
     *
     * @param accessLevelEntity encja poziomu dostępu.
     * @param info              wymagane dane dla danego poziomu dostępu.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private void grantAccessLevel(AccessLevelEntity accessLevelEntity, String info) throws AppException {
        if (accessLevelEntity.isActive())
            throw new AccessLevelAlreadyGrantedException();
        else {
            accessLevelEntity.setActive(true);
            if (accessLevelEntity instanceof AdminEntity) {
                AdminEntity adminEntity = ((AdminEntity) accessLevelEntity);
                adminEntity.setCardNumber(info);
                adminFacadeReadCommitted.edit(adminEntity);
            } else if (accessLevelEntity instanceof EmployeeEntity) {
                EmployeeEntity employeeEntity = ((EmployeeEntity) accessLevelEntity);
                employeeEntity.setWorkPhoneNumber(info);
                employeeFacadeReadCommitted.edit(employeeEntity);
            } else if (accessLevelEntity instanceof CustomerEntity) {
                CustomerEntity customerEntity = ((CustomerEntity) accessLevelEntity);
                customerEntity.setPhoneNumber(info);
                customerFacadeReadCommitted.edit(customerEntity);
            }
        }
    }

    /**
     * Metoda dodająca poziom dostepu.
     *
     * @param accessLevel poziom dostępu.
     * @param account     konto użytkownika, któremu nadajemy poziom dostępu.
     * @param info        wymagane dane dla danego poziomu dostępu.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private void grantAccessLevel(String accessLevel, AccountEntity account, String info) throws AppException {
        switch (accessLevel) {
            case "ROLE_ADMIN":
                AdminEntity adminEntity = new AdminEntity(account, info);
                adminEntity.setActive(true);
                account.getAccessLevels().add(adminEntity);
                adminFacadeReadCommitted.create(adminEntity);
                break;
            case "ROLE_EMPLOYEE":
                EmployeeEntity employeeEntity = new EmployeeEntity(account, info);
                employeeEntity.setActive(true);
                account.getAccessLevels().add(employeeEntity);
                employeeFacadeReadCommitted.create(employeeEntity);
                break;
            case "ROLE_CUSTOMER":
                CustomerEntity customerEntity = new CustomerEntity(account, info);
                customerEntity.setActive(true);
                account.getAccessLevels().add(customerEntity);
                customerFacadeReadCommitted.create(customerEntity);
                break;
            default:
                throw new GrantAccessLevelInvalidRoleException();
        }
    }

    /**
     * Metoda weryfikująca możliwość nadania poziomu dostępu.
     *
     * @param accessLevel poziom dostępu.
     * @param login       login użytkownika.
     * @param info        wymagane dane dla danego poziomu dostępu.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private void checkGrantIsAvailable(String accessLevel, String login, String info) throws AppException {
        switch (accessLevel) {
            case "ROLE_ADMIN":
                Optional<AdminEntity> adminEntity = adminFacadeReadCommitted.findByCardNumber(info);
                if (adminEntity.isPresent() && !adminEntity.get().getAccount().getLogin().equals(login))
                    throw new CardNumberIsAlreadyTakenException();
                break;
            case "ROLE_EMPLOYEE":
                Optional<EmployeeEntity> employeeEntity = employeeFacadeReadCommitted.findByWorkPhoneNumber(info);
                if (employeeEntity.isPresent() && !employeeEntity.get().getAccount().getLogin().equals(login))
                    throw new WorkPhoneNumberIsAlreadyTakenException();
                break;
            case "ROLE_CUSTOMER":
                Optional<CustomerEntity> customerEntity = customerFacadeReadCommitted.findByPhoneNumber(info);
                if (customerEntity.isPresent() && !customerEntity.get().getAccount().getLogin().equals(login))
                    throw new PhoneNumberIsAlreadyTakenException();
                break;
            default:
                throw new GrantAccessLevelInvalidRoleException();
        }
    }


    /**
     * Metoda weryfikująca możliwość nadania poziomu dostępu.
     *
     * @param accessLevelEntity poziom dostępu.
     * @param login             login nowego użytkownika.
     * @throws AppException jeśli operacja zakończy się niepowodzeniem.
     */
    private void checkGrantIsAvailable(AccessLevelEntity accessLevelEntity, String login) throws AppException {
        if (accessLevelEntity instanceof AdminEntity) {
            Optional<AdminEntity> ownerAdminEntity = adminFacadeReadCommitted.findByCardNumber(((AdminEntity) accessLevelEntity).getCardNumber());
            if (!this.checkUniqueCardNumber(ownerAdminEntity, login)) {
                throw new CardNumberIsAlreadyTakenException();
            }
        }
        if (accessLevelEntity instanceof CustomerEntity) {
            Optional<CustomerEntity> ownerCustomerEntity = customerFacadeReadCommitted.findByPhoneNumber(((CustomerEntity) accessLevelEntity).getPhoneNumber());
            if (!this.checkUniquePhoneNumber(ownerCustomerEntity, login)) {
                throw new PhoneNumberIsAlreadyTakenException();
            }
        }
        if (accessLevelEntity instanceof EmployeeEntity) {
            Optional<EmployeeEntity> ownerEmployeeEntity = employeeFacadeReadCommitted.findByWorkPhoneNumber(((EmployeeEntity) accessLevelEntity).getWorkPhoneNumber());
            if (!this.checkUniqueWorkPhoneNumber(ownerEmployeeEntity, login)) {
                throw new WorkPhoneNumberIsAlreadyTakenException();
            }
        }
    }


    /**
     * Metoda sprawdzająca czy numer karty jest unikalny bądź nie został zedytowany.
     *
     * @param ownerAdminEntity   ewentualny właściciel numer karty.
     * @param editedAccountLogin login użytkownika którego dane są edytowane.
     * @return wartość true jeśli numer należy do użytkownika lub nie występuje w bazie
     * false jeśli występuje u innego użytkownika.
     */
    private boolean checkUniqueCardNumber(Optional<AdminEntity> ownerAdminEntity, String editedAccountLogin) {
        if (ownerAdminEntity.isPresent()) {
            AdminEntity adminEntity = ownerAdminEntity.get();
            if (!adminEntity.getAccount().getLogin().equals(editedAccountLogin)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Metoda sprawdzająca czy numer telefonu jest unikalny bądź nie został zedytowany.
     *
     * @param ownerCustomerEntity ewentualny właściciel numer telefonu.
     * @param editedAccountLogin  login użytkownika którego dane są edytowane.
     * @return wartość true jeśli numer należy do użytkownika lub nie występuje w bazie
     * false jeśli występuje u innego użytkownika.
     */
    private boolean checkUniquePhoneNumber(Optional<CustomerEntity> ownerCustomerEntity, String editedAccountLogin) {
        if (ownerCustomerEntity.isPresent()) {
            CustomerEntity customerEntity = ownerCustomerEntity.get();
            if (!customerEntity.getAccount().getLogin().equals(editedAccountLogin)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Metoda sprawdzająca czy firmowy numer telefonu jest unikalny bądź nie został zedytowany.
     *
     * @param ownerEmployeeEntity ewentualny właściciel firmowego numer telefonu.
     * @param editedAccountLogin  login użytkownika którego dane są edytowane.
     * @return wartość true jeśli numer należy do użytkownika lub nie występuje w bazie
     * false jeśli występuje u innego użytkownika.
     */
    private boolean checkUniqueWorkPhoneNumber(Optional<EmployeeEntity> ownerEmployeeEntity, String editedAccountLogin) {
        if (ownerEmployeeEntity.isPresent()) {
            EmployeeEntity customerEntity = ownerEmployeeEntity.get();
            if (!customerEntity.getAccount().getLogin().equals(editedAccountLogin)) {
                return false;
            }
        }
        return true;
    }
}
