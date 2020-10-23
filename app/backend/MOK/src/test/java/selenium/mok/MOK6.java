package selenium.mok;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Odblokuj konto
public class MOK6 {

    private ChromeDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://localhost:8181/";

    private String adminUsername;
    private String adminPassword;

    private static final String ALTERED_ACCOUNT_SURNAME = "SeleniumBlocked";

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        driver = new ChromeDriver(new ChromeOptions().addArguments("--lang= pl", "--start-maximized", "--ignore-certificate-errors"));

        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void shouldUnlockAccount() throws InterruptedException {
        driver.get(BASE_URL);

        wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));

        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        synchronized (driver) {
            driver.wait(30000);
        }

        // Oczekiwanie na załadowanie listy kont
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("accountsTable")));

        // Wyszukanie użytkownika o danym nazwisku
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(ALTERED_ACCOUNT_SURNAME);
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(Keys.RETURN);

        synchronized (driver) {
            driver.wait(3000);
        }

        driver.findElement(By.name("activateCheckbox")).click();

        synchronized (driver) {
            driver.wait(3000);
        }

        assertTrue(driver.findElements(By.name("successSnackbar")).size() > 0);
        assertEquals(Boolean.toString(true), driver.findElement(By.name("activateCheckbox")).getAttribute("checked"));
    }
}
