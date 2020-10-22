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


// Resetuj hasło
public class MOK7 {

    private ChromeDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://localhost:8181/";
    private static final String NEW_PASSWORD = "ZAQ!2wsx";

    private String adminUsername;
    private String adminPassword;
    private String newEmail;
    private String urlForResetPass;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        options.addArguments("--lang= pl", "--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        //pobranie zmiennych środowiskowych
        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");
        wait = new WebDriverWait(driver, 100);
    }

    @Test
    public void shouldResetPassword() throws InterruptedException {
        prepareAccountForResetPassword();
        //przejście na stronę resetu hasła
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Zapomniałeś hasła?")));
        driver.findElement(By.linkText("Zapomniałeś hasła?")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("resetButton")));
        driver.findElement(By.name("email")).sendKeys(newEmail);
        driver.findElement(By.name("resetButton")).click();
        // Oczekiwanie na komunikat o pozytywnym zakończeniu operacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));
        synchronized (driver) {
            driver.wait(5000);
        }
        EmailUtils.clickOnFirstLink(driver, wait);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("resetPass")));
        urlForResetPass = driver.getCurrentUrl();
        resetPassword(NEW_PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logowanie")));
        driver.findElement(By.linkText("Logowanie")).click();
        TestUtils.logIn(driver, adminUsername, NEW_PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        TestUtils.logOut(driver, wait);

    }

    @AfterEach
    public void tearDown() {
        driver.get(urlForResetPass);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("resetPass")));
        resetPassword(adminPassword);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Logowanie")));
        driver.quit();
    }

    private void prepareAccountForResetPassword() throws InterruptedException {
        EmailUtils.copyTempEmailAddressToClipboard(driver);
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        TestUtils.logIn(driver, adminUsername, adminPassword);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        TestUtils.goToOwnAccountDetails(driver, wait);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("edit")));
        driver.findElement(By.name("changeEmail")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("newEmail")));
        TestUtils.clearTextField(driver, "newEmail");
        WebElement emailField = driver.findElement(By.name("newEmail"));
        emailField.sendKeys(Keys.CONTROL, "v");
        newEmail = emailField.getAttribute("value");
        driver.findElement(By.name("changeEmailSubmit")).click();
        driver.findElement(By.name("confirmButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));
        synchronized (driver) {
            driver.wait(7000);
        }
        EmailUtils.clickOnLink(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("submitButton")));
        driver.findElement(By.name("submitButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        TestUtils.logOut(driver, wait);
    }

    private void resetPassword(String pass){
        driver.findElement(By.name("newPassword")).sendKeys(pass);
        driver.findElement(By.name("repeatNewPassword")).sendKeys(pass);
        driver.findElement(By.name("resetPass")).click();
    }

}
