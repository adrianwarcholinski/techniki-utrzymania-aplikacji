package selenium.mok;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utils.EmailUtils;
import selenium.utils.StrongPasswordGenerator;

// Utwórz konto innego użytkownika
public class MOK2 {

    private ChromeDriver driver;

    private static final String BASE_URL = "https://localhost:8181/";

    private static final int LOGIN_AND_EMAIL_SUFFIX_LENGTH = 10;

    private String login;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;

    private String adminUsername;
    private String adminPassword;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", System.getenv("WEBDRIVER_CHROME_DRIVER"));
        driver = new ChromeDriver(new ChromeOptions().addArguments("--lang= pl", "--start-maximized", "--ignore-certificate-errors"));

        adminUsername = System.getenv("SELENIUM_USERNAME");
        adminPassword = System.getenv("SELENIUM_PASSWORD");

        String loginAndEmailSuffix = new RandomStringGenerator.Builder()
                .withinRange('a', 'z')
                .build()
                .generate(LOGIN_AND_EMAIL_SUFFIX_LENGTH);

        login = "selenium" + loginAndEmailSuffix;
        firstName = "SeleniumFirstName";
        lastName = "SeleniumLastName";

        password = StrongPasswordGenerator.generate();

        phoneNumber = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .build()
                .generate(9);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void shouldCreateAccount() throws InterruptedException {
        EmailUtils.copyTempEmailAddressToClipboard(driver);

        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, 40);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("signInButton")));

        // Logowanie
        driver.findElement(By.name("login")).sendKeys(adminUsername);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.name("signInButton")).click();

        // Oczekiwanie na załadowanie dashboardu
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("logo")));

        driver.findElement(By.name("addNewAccountButton")).click();

        // Wypełnienie formularza
        driver.findElement(By.name("login")).sendKeys(login);
        driver.findElement(By.name("firstName")).sendKeys(firstName);
        driver.findElement(By.name("lastName")).sendKeys(lastName);
        driver.findElement(By.name("emailAddress")).sendKeys(Keys.CONTROL, "v");
        driver.findElement(By.name("phoneNumber")).sendKeys(phoneNumber);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("repeatedPassword")).sendKeys(password);

        // Kliknięcie przycisku "Zarejestruj"
        driver.findElement(By.name("submitButton")).click();

        synchronized (driver) {
            driver.wait(1000);
        }

        // Potwierdzenie rejestracji
        driver.findElement(By.name("confirmButton")).click();

        // Oczekiwanie na komunikat o pozytywnym zakończeniu operacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("successSnackbar")));

        // Wylogowanie się
        driver.findElement(By.cssSelector(".account-button > .MuiButton-label")).click();
        driver.findElement(By.cssSelector(".MuiButtonBase-root:nth-child(2) > .MuiSvgIcon-root")).click();

        EmailUtils.clickOnLink(driver);

        // Oczekiwanie na wiadomość o skutecznej weryfikacji konta
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiTypography-body1")));
    }
}