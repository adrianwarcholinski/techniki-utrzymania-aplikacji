package selenium.mok;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utils.TestUtils;
import selenium.utils.EmailUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Zmień adres e-mail swojego konta
public class MOK10 {
    private ChromeDriver driver;

    private static final String BASE_URL = "https://localhost:8181/";

    private String adminUsername;
    private String adminPassword;
    private String newEmail;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        options.addArguments("--lang= pl", "--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        //pobranie zmiennych środowiskowych
        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");
    }

    @Test
    public void shouldChangeOwnEmail() throws InterruptedException {
        EmailUtils.copyTempEmailAddressToClipboard(driver);

        driver.get(BASE_URL);
        WebDriverWait wait = new WebDriverWait(driver, 80);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));

        // Logowanie
        TestUtils.logIn(driver, adminUsername, adminPassword);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        //przejście do własnych danych
        TestUtils.goToOwnAccountDetails(driver, wait);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("edit")));
        //otwarcie zmiany maila
        driver.findElement(By.name("changeEmail")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("newEmail")));

        // Przejście do pola zmiany miala użytkownika
        TestUtils.clearTextField(driver, "newEmail");

        WebElement emailField = driver.findElement(By.name("newEmail"));
        emailField.sendKeys(Keys.CONTROL, "v");
        // Zapisanie nowego maila
        newEmail = emailField.getAttribute("value");

        // Kliknięcie przycisku zmiany maila
        driver.findElement(By.name("changeEmailSubmit")).click();

        // Potwierdzenie operacji
        driver.findElement(By.name("confirmButton")).click();

        // Oczekiwanie na komunikat o pozytywnym zakończeniu operacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));

        // Oczekiwanie 5 sekund na przyjście e-maila
        synchronized (driver) {
            driver.wait(5000);
        }
        //Kliknięcie linku weryfikacyjnego i sprawdzenie wiadomości czy udało się zminić email
        EmailUtils.clickOnLink(driver);
        //Informacja o zmianie
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("submitButton")));
        driver.findElement(By.name("submitButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        //przejście do własnych danych
        TestUtils.goToOwnAccountDetails(driver, wait);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("edit")));

        driver.findElement(By.name("changeEmail")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("newEmail")));
        //sprawdzenie czy ustawiono nowy email
        assertEquals(newEmail, driver.findElement(By.name("currentEmail")).getAttribute("value"));

    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

}