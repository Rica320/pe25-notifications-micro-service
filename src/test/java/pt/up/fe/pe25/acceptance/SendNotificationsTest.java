package pt.up.fe.pe25.acceptance;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.Select;
import java.util.List;

@QuarkusTest
public class SendNotificationsTest {

    private final String url = "http://localhost:8080/view";

    @Test
    public void sendNotificationTest() {

        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();

        try {
            // Open the page
            driver.get(url);

            WebElement serviceSelect = driver.findElement(By.id("service"));
            WebElement receiverEmailsInput = driver.findElement(By.id("receiverEmails"));
            WebElement phoneListInput = driver.findElement(By.id("phoneList"));
            WebElement messageInput = driver.findElement(By.id("message"));
            WebElement submitButton = driver.findElement(By.cssSelector("input[type=submit]"));

            Select select = new Select(serviceSelect);

            List<WebElement> options = select.getOptions();
            for (WebElement option : options) {
                option.click();
            }

            receiverEmailsInput.sendKeys("test@example.com");
            phoneListInput.sendKeys("123456789");
            messageInput.sendKeys("This is a test message");
            submitButton.click();

        } finally {
            driver.quit();
        }
    }
}
