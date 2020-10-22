package selenium.mok;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;
import selenium.utils.EmailUtils;

// Zmień adres e-mail konta innego użytkownika
public class MOK14 {
    private ChromeDriver driver;

    private static final String BASE_URL = "https://localhost:8181/";

    private String adminUsername;
    private String adminPassword;
    private String userUsername;
    private String newEmail;


    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        options.addArguments("--lang= pl", "--ignore-certificate-errors", "--start-maximized");
        driver = new ChromeDriver(options);
        //pobranie zmiennych środowiskowych
        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");
        userUsername = System.getenv("SELENIUM_TWO_USERNAME");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void shouldChangeEmail() throws InterruptedException {
        EmailUtils.copyTempEmailAddressToClipboard(driver);

        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));

        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        // Przejście do pola zmiany miala użytkownika
        WebElement emailField = goToEmailChangeField(wait);

        // Usunięcie zawartości pola i podanie nowego maila
        emailField.sendKeys(Keys.CONTROL + "a");
        emailField.sendKeys(Keys.DELETE);
        emailField.sendKeys(Keys.CONTROL, "v");
        // Zapisanie nowego maila
        newEmail = emailField.getAttribute("value");

        // Kliknięcie przycisku zmiany maila
        driver.findElement(By.name("handleEmailChangeButton")).click();

        // Potwierdzenie operacji
        driver.findElement(By.name("confirmButton")).click();

        // Oczekiwanie 5 sekund na przyjście e-maila
        synchronized (driver) {
            driver.wait(5000);
        }

        // Oczekiwanie na komunikat o pozytywnym zakończeniu operacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));

        // Oczekiwanie 5 sekund na przyjście e-maila
        synchronized (driver) {
            driver.wait(5000);
        }

        //Kliknięcie linku weryfikacyjnego i sprawdzenie wiadomości czy udało się zminić email
        EmailUtils.clickOnLink(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiTypography-body1")));
        assertThat(driver.findElement(By.cssSelector(".MuiTypography-body1")).getText(), is("Pomyślnie zmieniono adres e-mail."));
        driver.findElement(By.cssSelector(".MuiButton-label")).click();
        //Przejście do pola z emailem użytkownika
        emailField = goToEmailChangeField(wait);
        // Sprawdzenie czy obecny mail się zmienił
        assertThat(emailField.getAttribute("value"), is(newEmail));

    }

    private WebElement goToEmailChangeField(WebDriverWait wait) {
        // Sprawdzenie czy jesteśmy na stronie głównej
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        // Przejście do widoku wszystkoich kont
        driver.findElement(By.name("showAllAccountsButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("showAllAccountsCardHeader")));
        // Wyszukanie użytkownika
        driver.findElement(By.name("showAllAccountSearchField")).click();
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys("seleniumowy");
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(Keys.ENTER);
        // Kliknięcie przycisku i przejście do szczegółow konta użytkownika
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(userUsername)));
        driver.findElement(By.name(userUsername)).findElement(By.name("showAccountDetailsButton")).click();
        // Otworzenie panelu zmiany maila
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("emailManagementPanel")));
        driver.findElement(By.name("emailManagementPanel")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("newEmail")));
        return driver.findElement(By.name("newEmail"));
    }
}
