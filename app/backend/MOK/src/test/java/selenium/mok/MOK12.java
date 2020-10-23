package selenium.mok;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utils.TestUtils;

// Zmień hasło konta innego użytkownika
public class MOK12 {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://localhost:8181/";
    private static final String NAME_AND_SURNAME = "Jakub Flaszka";
    private static final String NEW_PASSWORD = "ZAQ!2wsx";

    private String adminUsername;
    private String adminPassword;
    private String otherAccountLogin;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        options.addArguments("--lang= pl", "--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 60);
        //pobranie zmiennych środowiskowych
        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");
    }

    @Test
    public void shouldChangeAccountPassword() throws InterruptedException {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        TestUtils.logIn(driver, adminUsername, adminPassword);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("showAllAccountSearchField")));
        //odnaleznienie użytkownika
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(NAME_AND_SURNAME);
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(Keys.RETURN);
        //odświerzenie listy
        synchronized (driver) {
            driver.wait(3000);
        }
        otherAccountLogin = driver.findElement(By.name("login")).getText();
        driver.findElement(By.name("showAccountDetailsButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("changePass")));
        changeAccountPassword();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));
        TestUtils.logOut(driver, wait);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        TestUtils.logIn(driver, otherAccountLogin, NEW_PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        TestUtils.logOut(driver, wait);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    private void changeAccountPassword() throws InterruptedException {
        driver.findElement(By.name("changePass")).click();
        //wysunięcie paska
        synchronized (driver) {
            driver.wait(2000);
        }
        driver.findElement(By.name("newPassword")).sendKeys(NEW_PASSWORD);
        driver.findElement(By.name("repeatNewPassword")).sendKeys(NEW_PASSWORD);
        driver.findElement(By.name("submitChangePass")).click();
        driver.findElement(By.name("confirmButton")).click();
    }

}

