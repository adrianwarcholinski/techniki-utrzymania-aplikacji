package selenium.mok;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utils.EmailUtils;
import selenium.utils.StrongPasswordGenerator;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Wyślij link weryfikacyjny
public class MOK21 {

    private ChromeDriver driver;

    private static final String BASE_URL = "https://localhost:8181/";
    private static final String EMAIL_URL = "https://10minemail.com/pl/";

    private static final int LOGIN_AND_EMAIL_SUFFIX_LENGTH = 10;

    private String login;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String adminUsername;
    private String adminPassword;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("intl.accept_languages", "pl");
        driver = new ChromeDriver(new ChromeOptions().addArguments("--lang=pl", "--start-maximized", "--ignore-certificate-errors").setExperimentalOption("prefs", prefs));
        String loginAndEmailSuffix = new RandomStringGenerator.Builder()
                .withinRange('a', 'z')
                .build()
                .generate(LOGIN_AND_EMAIL_SUFFIX_LENGTH);

        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");

        login = "selenium" + loginAndEmailSuffix;
        firstName = "SeleniumFirstName";
        lastName = "Selenium" + loginAndEmailSuffix;

        password = StrongPasswordGenerator.generate();

        phoneNumber = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .build()
                .generate(9);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void shouldRegister() throws InterruptedException {
        EmailUtils.copyTempEmailAddressToClipboard(driver);

        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 40);

        // Utworzneie nowego konta
        createAccount(wait);

        // Przejście do formularza logowania
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("loginLink")));
        driver.findElement(By.name("loginLink")).click();

        // Logowanie
        signIn(wait);

        // Sprawdzenie czy checkbox oznaczający veryfikację utworzonego konta jest niezaznaczony
        assertFalse(isChecked(wait));
        // Kliknięcie w przycisk do ponownego wysłani maila z linkiem weryfikacyjnym
        driver.findElement(By.name(login)).findElement(By.name("sendVerificationLinkButton")).click();
        // Oczekiwanie na pomyślne zakończenie operacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));

        // Wyloguj się
        driver.findElement(By.name("AccountMenu")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logOut")));
        driver.findElement(By.name("logOut")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiTypography-h4")));
        // Oczekiwanie 5 sekund na przyjście e-maila
        synchronized (driver) {
            driver.wait(5000);
        }

        // Przejście na skrzynkę mailową i sprawdzenie czy są tam dwa maile z linkiem weryfikacyjnym
        driver.get(EMAIL_URL);
        assertThat(driver.findElement(By.cssSelector("li:nth-child(3) .inboxSubject > .viewLink")).getText(), is("Weryfikacja konta"));
        assertThat(driver.findElement(By.cssSelector("li:nth-child(2) .inboxSubject > .viewLink")).getText(), is("Weryfikacja konta"));

        // Kliknięcie linku weryfikacyjnego w najnowszym mailu
        driver.findElements(By.className("viewLink")).get(4).click();
        synchronized (driver) {
            driver.wait(5000);
        }
        driver.findElement(By.name("emailUrl")).click();
        // Oczekiwanie na potwierdzenie weryfikacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiTypography-body1")));
        driver.findElement(By.cssSelector(".MuiButton-label")).click();

        // Logowanie
        signIn(wait);

        // Sprawdzenie czy checkbox oznaczający veryfikację utworzonego konta jest odznaczony
        assertTrue(isChecked(wait));
    }

    private void signIn(WebDriverWait wait){
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        // Sprawdzenie czy jesteśmy na stronie głównej
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
    }

    private boolean isChecked(WebDriverWait wait){
        // Otworzenie widoku wszystkich kont i wyszukanie po nazwisku utworzonego konta
        driver.findElement(By.name("showAllAccountsButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("showAllAccountsCardHeader")));
        driver.findElement(By.name("showAllAccountSearchField")).click();
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(lastName);
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(Keys.ENTER);
        // Poczekanie aż konto zostanei wyszukane i zwrócenie flagi czy checkbox jest zaznaczony
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(login)));
        return  driver.findElement(By.name(login)).findElement(By.name("verifiedCheckbox")).isSelected();
    }

    private void createAccount(WebDriverWait wait){
        // Przejście na stronę rejestracji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Zarejestruj się")));
        driver.findElement(By.linkText("Zarejestruj się")).click();

        // Wypełnienie formularza rejestracji
        driver.findElement(By.name("login")).sendKeys(login);
        driver.findElement(By.name("firstName")).sendKeys(firstName);
        driver.findElement(By.name("lastName")).sendKeys(lastName);
        driver.findElement(By.name("emailAddress")).sendKeys(Keys.CONTROL, "v");
        driver.findElement(By.name("phoneNumber")).sendKeys(phoneNumber);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("repeatedPassword")).sendKeys(password);

        // Kliknięcie przycisku "Zarejestruj"
        driver.findElement(By.name("submitButton")).click();

        // Potwierdzenie rejestracji
        driver.findElement(By.name("confirmButton")).click();

        // Oczekiwanie na komunikat o pozytywnym zakończeniu operacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));
    }
}