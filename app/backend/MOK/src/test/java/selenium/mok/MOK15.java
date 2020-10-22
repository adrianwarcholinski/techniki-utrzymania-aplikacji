package selenium.mok;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

// Przeglądaj szczegóły konta innego użytkownika
public class MOK15 {
    private WebDriver driver;

    private static final String BASE_URL = "https://localhost:8181/";

    private String adminUsername;
    private String adminPassword;
    private String userUsername;


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
    public void shouldDisplayAccountDetails() {
        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        // Sprawdzenie czy jestesmy na stronie głównej
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiCardHeader-title")));
        // Sprawdzenie czy login jest w nagłówku karty
        assertThat(driver.findElement(By.cssSelector(".MuiCardHeader-title")).getText(), is("Dane konta " + userUsername));
        // Sprawdzenie wpisu w okruszkach chleba i URL
        assertThat(driver.findElement(By.cssSelector(".MuiBreadcrumbs-li > .MuiTypography-body1")).getText(), is("Szczegóły konta"));
        assertThat(driver.getCurrentUrl(), is(BASE_URL + "dashboard/accounts/details/" + userUsername));
        // Sprawdzenie czy pola na imię i nazwisko nie są puste
        WebElement nameElement = driver.findElement(By.name("name"));
        WebElement surnameElement = driver.findElement(By.name("surname"));
        assertFalse(nameElement.getAttribute("value").isBlank());
        assertFalse(surnameElement.getAttribute("value").isBlank());
        //Sprawdzenie czy pola są nieedytowalne
        assertFalse(nameElement.isEnabled());
        assertFalse(surnameElement.isEnabled());
    }
}
