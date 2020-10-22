package selenium.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Collections;
import java.util.List;

public class EmailUtils {

    private static final String EMAIL_URL = "https://10minemail.com/pl/";

    public static void copyTempEmailAddressToClipboard(WebDriver driver) throws InterruptedException {
        driver.get(EMAIL_URL);
        driver.findElement(By.className("refresh-countdown")).click();
        synchronized (driver) {
            driver.wait(2000);
        }
        driver.findElement(By.cssSelector(".btn-rds:nth-child(1) path")).click();
    }

    public static void clickOnLink(ChromeDriver driver) throws InterruptedException {
        driver.get(EMAIL_URL);
        driver.findElements(By.className("viewLink")).get(4).click();
        synchronized (driver) {
            driver.wait(5000);
        }
        driver.findElement(By.name("emailUrl")).click();
    }

    public static void clickOnFirstLink(ChromeDriver driver, WebDriverWait wait) throws InterruptedException {
        driver.get(EMAIL_URL);
        List<WebElement> webElements = Collections.singletonList(driver.findElements(By.className("viewLink")).get(4));
        webElements.get(0).click();
        synchronized (driver) {
            driver.wait(5000);
        }
        driver.findElement(By.name("emailUrl")).click();
    }
}
