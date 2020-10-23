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

import static org.junit.jupiter.api.Assertions.assertEquals;

// Edytuj szczegóły swojego konta
public class MOK9 {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://localhost:8181/";

    private String adminUsername;
    private String adminPassword;
    private String oldName;
    private String oldSurName;
    private String oldPhoneNumber;
    private String oldCardNumber;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        options.addArguments("--lang= pl", "--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 30);
        //pobranie zmiennych środowiskowych
        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");
    }

    @Test
    public void shouldChangeOwnAccountDetails() throws InterruptedException {
        String[] values = {"test", "testowy", "abc-def-zxcv", "123412345"};
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));
        TestUtils.logIn(driver, adminUsername, adminPassword);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));
        TestUtils.goToOwnAccountDetails(driver, wait);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("edit")));
        saveOldValues();
        driver.findElement(By.name("edit")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("back")));
        changeValues(values);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));
        //czekanie na odświeżenie danych
        synchronized (driver) {
            driver.wait(3000);
        }
        assertEquals(values[0], driver.findElement(By.name("name")).getAttribute("value"));
        assertEquals(values[1], driver.findElement(By.name("surname")).getAttribute("value"));
        assertEquals(values[2], driver.findElement(By.name("cardNumber")).getAttribute("value"));
        assertEquals(values[3], driver.findElement(By.name("phoneNumber")).getAttribute("value"));
    }

    @AfterEach
    public void tearDown() {
        driver.findElement(By.name("edit")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("back")));
        changeValues(new String[]{oldName, oldSurName, oldCardNumber, oldPhoneNumber});
        driver.quit();
    }



    private void saveOldValues() {
        oldName = driver.findElement(By.name("name")).getAttribute("value");
        oldSurName = driver.findElement(By.name("surname")).getAttribute("value");
        oldPhoneNumber = driver.findElement(By.name("phoneNumber")).getAttribute("value");
        oldCardNumber = driver.findElement(By.name("cardNumber")).getAttribute("value");
    }

    private void changeValues(String[] values) {
        TestUtils.clearTextField(driver,"name");
        driver.findElement(By.name("name")).sendKeys(values[0]);
        TestUtils.clearTextField(driver,"surname");
        driver.findElement(By.name("surname")).sendKeys(values[1]);
        TestUtils.clearTextField(driver,"cardNumber");
        driver.findElement(By.name("cardNumber")).sendKeys(values[2]);
        TestUtils.clearTextField(driver,"phoneNumber");
        driver.findElement(By.name("phoneNumber")).sendKeys(values[3]);
        driver.findElement(By.name("submit")).click();
        driver.findElement(By.name("confirmButton")).click();
    }


}
