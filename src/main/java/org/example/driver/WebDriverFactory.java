package org.example.driver;

import org.example.model.BrowserType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class WebDriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserType> browserThreadLocal = new ThreadLocal<>();
    
    private static final Map<BrowserType, Semaphore> browserSemaphores = new EnumMap<>(BrowserType.class);
    
    static {
        browserSemaphores.put(BrowserType.CHROME, new Semaphore(1));
        browserSemaphores.put(BrowserType.FIREFOX, new Semaphore(1));
    }
    
    public static WebDriver getDriver(BrowserType browserType) {
        if (driverThreadLocal.get() == null) {
            // Acquire semaphore for this browser type
            Semaphore semaphore = browserSemaphores.get(browserType);
            try {
                semaphore.acquire();
                
                WebDriver driver = createDriver(browserType);
                driverThreadLocal.set(driver);
                browserThreadLocal.set(browserType);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to acquire semaphore for browser: " + browserType, e);
            } catch (Exception e) {
                // Release semaphore if driver creation fails
                semaphore.release();
                throw new RuntimeException("Failed to create WebDriver for browser: " + browserType, e);
            }
        }
        return driverThreadLocal.get();
    }
    
    private static WebDriver createDriver(BrowserType browserType) {
        switch (browserType) {
            case CHROME:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");

                chromeOptions.addArguments("--headless=new");
                return new ChromeDriver(chromeOptions);
                
            case FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("-headless");
                WebDriver driver = new FirefoxDriver(firefoxOptions);
                driver.manage().window().maximize();
                return driver;
                
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }

}