package selenium.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestUtils {
    public static void logIn(WebDriver driver, String login, String pass) {
        driver.findElement(By.name("login")).sendKeys(login);
        driver.findElement(By.name("password")).sendKeys(pass);
        driver.findElement(By.name("signInButton")).click();
    }

    public static void goToAccountDetails(WebDriver driver, String name) throws InterruptedException {
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(name);
        driver.findElement(By.name("showAllAccountSearchField")).sendKeys(Keys.RETURN);
        //odświerzenie listy
        synchronized (driver) {
            driver.wait(3000);
        }
        driver.findElement(By.name("showAccountDetailsButton")).click();
    }

    public static void clearTextField(WebDriver driver, String name) {
        driver.findElement(By.name(name)).sendKeys(Keys.CONTROL + "a");
        driver.findElement(By.name(name)).sendKeys(Keys.DELETE);
    }

    public static void logOut(WebDriver driver, WebDriverWait wait) {
        driver.findElement(By.name("AccountMenu")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("ownAccount")));
        driver.findElement(By.name("logOut")).click();
    }

    public static void goToOwnAccountDetails(WebDriver driver, WebDriverWait wait) {
        driver.findElement(By.name("AccountMenu")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("ownAccount")));
        driver.findElement(By.name("ownAccount")).click();
    }
}
