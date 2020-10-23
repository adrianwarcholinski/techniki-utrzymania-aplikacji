package selenium.mok;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

// Wyświetl raport na temat ostatnich uwierzytelnień użytkowników
public class MOK20 {
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
    public void shouldDisplayReport() {
        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        // Zalogoweanie i wylogowanie żeby upenić się, że na liscie bedą dane
        // Logowanie
        logIn(wait);
        // Wylogowanie
        driver.findElement(By.name("AccountMenu")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logOut")));
        driver.findElement(By.name("logOut")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiTypography-h4")));
        // Ponowne zalogowanie
        logIn(wait);

        // Sprawdzenie czy jestesmy na stronie głównej
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));

        // Przejście do widoku raportu
        driver.findElement(By.name("adminReportButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiCardHeader-title")));
        // Sprawdzenie czy zgadza się text nagłówka i URL
        WebElement cardHeader = driver.findElement((By.cssSelector(".MuiCardHeader-title")));
        assertThat(cardHeader.getText(), is("Raport kont"));
        assertThat(driver.getCurrentUrl(), is(BASE_URL + "dashboard/report"));
        // Sprawdzenie czy lista nie jest pusta
        List<WebElement> list = driver.findElements(By.cssSelector(".MuiTableRow-root"));
        assertTrue(list.size() > 0);

    }

    private void logIn(WebDriverWait wait) {
        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
    }
}
