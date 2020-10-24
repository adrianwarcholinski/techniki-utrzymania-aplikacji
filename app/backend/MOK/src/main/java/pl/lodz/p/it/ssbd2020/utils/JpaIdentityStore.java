package pl.lodz.p.it.ssbd2020.utils;

import pl.lodz.p.it.ssbd2020.entities.AccessLevelEntity;
import pl.lodz.p.it.ssbd2020.entities.AccountEntity;
import pl.lodz.p.it.ssbd2020.exceptions.AppException;
import pl.lodz.p.it.ssbd2020.mok.facades.interfaces.AccountFacadeReadCommittedLocal;
import pl.lodz.p.it.ssbd2020.mok.managers.AccountManager;
import pl.lodz.p.it.ssbd2020.mok.managers.interfaces.AccountManagerLocal;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class JpaIdentityStore implements IdentityStore {

    @Inject
    private AccountManagerLocal accountManager;

    @Inject
    private HashGenerator hashGenerator;

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        return IdentityStore.super.getCallerGroups(validationResult);
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
            try {
                AccountEntity account = accountManager.getAccountDetails(usernamePasswordCredential.getCaller());
                Set<String> groups = account.getAccessLevels().stream()
                        .map(AccessLevelEntity::getLevel)
                        .collect(Collectors.toSet());
                if (usernamePasswordCredential.getCaller().equals(account.getLogin()) &&
                        hashGenerator.verifyPassword(usernamePasswordCredential.getPasswordAsString(),account.getPassword())) {
                    return new CredentialValidationResult(account.getLogin(), groups);
                }
            } catch (AppException e) {
                return CredentialValidationResult.INVALID_RESULT;
            }
        }
        return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
}