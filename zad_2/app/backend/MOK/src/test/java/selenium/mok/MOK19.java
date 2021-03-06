package selenium.mok;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// Wyloguj się
public class MOK19 {
    private WebDriver driver;

    private static final String BASE_URL = "https://localhost:8181/";

    private String adminUsername;
    private String adminPassword;


    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        options.addArguments("--lang= pl", "--ignore-certificate-errors", "--start-maximized");
        driver = new ChromeDriver(options);
        //pobranie zmiennych środowiskowych
        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void shouldLogOut() {
        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();
        // Sprawdzenie czy jesteśmy na stronie głównej
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        //Kliknięcie przycisku wylogowującego
        driver.findElement(By.name("AccountMenu")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logOut")));
        driver.findElement(By.name("logOut")).click();
        // Sprawdzenie czy zostaliśmy przekierowani na stronę logowania
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiTypography-h4")));
        assertThat(driver.getCurrentUrl(), is(BASE_URL + "login"));
    }
}
