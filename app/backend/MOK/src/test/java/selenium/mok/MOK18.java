package selenium.mok;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Zaloguj się
public class MOK18 {
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
    public void shouldLogIn() {
        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        // Logowanie
        logIn(wait);
        // Sprawdzenie czy jesteśmy na stronie głównej
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        // Sprawdzenie że url wskazuje na dashboard
        assertThat(driver.getCurrentUrl(), CoreMatchers.startsWith("https://localhost:8181/dashboard"));

        // Wylogowanie
        driver.findElement(By.name("AccountMenu")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logOut")));
        driver.findElement(By.name("logOut")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiTypography-h4")));
        // Ponowne zalogowanie
        logIn(wait);
        // Sprawdzenie czy wyświetli się snackbar z inforamcją o ostatnim pmyślnym zalogowaniu
        List<WebElement> snackbarList = driver.findElements(By.name("infoSnackbar"));
        assertTrue(snackbarList.size() > 0);
        assertThat(snackbarList.get(0).getText(), CoreMatchers.containsString("Ostatnia poprawna próba logowania:"));

    }

    private void logIn(WebDriverWait wait){
        //Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
    }
}
