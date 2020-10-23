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
import selenium.utils.TestUtils;

// Zmień hasło swojego konta
public class MOK8 {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://localhost:8181/";
    private static final String NEW_PASSWORD = "ZAQ!2wsx";

    private String adminUsername;
    private String adminPassword;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        options.addArguments("--lang= pl", "--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 30);
        //pobranie zmiennych środowiskowych
        adminUsername = System.getenv("SELENIUM_TWO_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");
    }

    @Test
    public void shouldChangeOwnAccountPassword() throws InterruptedException {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        TestUtils.logIn(driver, adminUsername, adminPassword);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        TestUtils.goToOwnAccountDetails(driver, wait);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("edit")));
        changeOwnPassword(NEW_PASSWORD, adminPassword);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));
        TestUtils.logOut(driver, wait);
        TestUtils.logIn(driver, adminUsername, NEW_PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        TestUtils.logOut(driver, wait);
        synchronized (driver) {
            driver.wait(2000);
        }
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        TestUtils.logIn(driver, adminUsername, NEW_PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        TestUtils.goToOwnAccountDetails(driver, wait);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("edit")));
        changeOwnPassword(adminPassword, NEW_PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));
        TestUtils.logOut(driver, wait);
        driver.quit();
    }

    private void changeOwnPassword(String newPass, String oldPass) throws InterruptedException {
        driver.findElement(By.name("changePass")).click();
        //wysunięcie paska
        synchronized (driver) {
            driver.wait(2000);
        }
        driver.findElement(By.name("oldPassword")).sendKeys(oldPass);
        driver.findElement(By.name("newPassword")).sendKeys(newPass);
        driver.findElement(By.name("repeatNewPassword")).sendKeys(newPass);
        driver.findElement(By.name("submitChangePass")).click();
        driver.findElement(By.name("confirmButton")).click();
    }
}
