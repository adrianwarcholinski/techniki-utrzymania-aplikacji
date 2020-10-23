package pl.lodz.p.it.ssbd2020.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Klasa reprezentuje konfigurację ról aplikacji.
 * W adnotacjach zdefiniowano dostępne role w aplikacji.
 * Oprócz tego określono źródło danych używane do autoryzacji, a także
 * kwerendy służące do pozyskania haseł i dostępnych ról.
 */
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "${'java:/jdbc/ssbd01payaraDS'}",
        callerQuery = "${'SELECT password FROM auth_view WHERE login = ?'}",
        groupsQuery = "${'SELECT level FROM auth_view WHERE login = ?'}",
        hashAlgorithm = PasswordHash.class
)
@ApplicationPath(value = "app")
@ApplicationScoped
public class ApplicationConfig extends Application {
}
