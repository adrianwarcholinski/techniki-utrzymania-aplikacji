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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

// Przeglądaj konta wszystkich użytkowników
public class MOK16 {
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
    public void shouldDisplayAllAccounts() {
        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        // Sprawdzenie czy jestesmy na stronie głównej
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        // Przejście do widoku wszystkich kont
        driver.findElement(By.name("showAllAccountsButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("showAllAccountsCardHeader")));
        assertThat(driver.getCurrentUrl(), is(BASE_URL + "dashboard/accounts"));
        // Sprawdzenie czy lista kont nie jest pusta

        List<WebElement> list = driver.findElements(By.cssSelector(".MuiTableRow-root"));
        assert(list.size() > 0);
        // Sprawdzenie przykładowego użytkownika
        driver.findElement(By.name("showAllAccountSearchField")).click();
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys("seleniumowy");
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(userUsername)));
        driver.findElement(By.name(userUsername)).findElement(By.name("showAccountDetailsButton"));

    }
}
