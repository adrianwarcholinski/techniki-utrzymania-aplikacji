package selenium.mok;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

// Przełącz poziom dostępu
public class MOK17 {
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
    public void shouldChangeRole() {
        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        // Sprawdzenie czy jesteśmy na stronie głównej
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        // Sprawdzenie kolorów i nazwy roli przed zmianą
        String activeRole = driver.findElement(By.name("activeRoleButton")).findElement(By.cssSelector(".MuiButton-label")).getText();
        assertThat(activeRole, is("Administrator"));
        assertThat(driver.findElement(By.name("activeRoleButton")).getCssValue("background-color"),is("rgba(244, 67, 54, 1)"));
        assertThat(driver.findElement(By.cssSelector(".MuiButtonBase-root:nth-child(3)")).getCssValue("background-color"),is("rgba(244, 67, 54, 1)"));
        // Zmiana roli
        driver.findElement(By.name("activeRoleButton")).click();
        driver.findElement(By.name("ROLE_CUSTOMER")).click();
        // Sprawdzenie kolorów i nazwy roli po zmianie
        assertThat(driver.findElement(By.name("activeRoleButton")).getCssValue("background-color"),is("rgba(76, 175, 80, 1)"));
        assertThat(driver.findElement(By.cssSelector(".MuiButtonBase-root:nth-child(3)")).getCssValue("background-color"),is("rgba(76, 175, 80, 1)"));
        activeRole = driver.findElement(By.name("activeRoleButton")).findElement(By.cssSelector(".MuiButton-label")).getText();
        assertThat(activeRole, is("Klient"));
    }
}
