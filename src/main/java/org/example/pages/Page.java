package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class Page {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;

    public Page(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.actions = new Actions(driver);
    }

    public abstract boolean isPageLoaded();

    protected boolean isElementDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void click(By locator) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Постепенный скролл до появления элемента
     * @param locator - xpath элемента, который ищем
     * @return WebElement - найденного элемента
     * @throws NoSuchElementException - если элемент так и не появился при скролле
     */
    protected WebElement scrollUntilFindElement(By locator) {

        int attempts = 0;
        final int maxAttempts = 10;
        final int scrollStep = 300;

        while (attempts < maxAttempts) {
            try {
                // Пытаемся найти элемент
                WebElement item = driver.findElement(locator);
                if (item.isDisplayed()) {
                    return item;
                }
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                // Элемент еще не найден - продолжаем скроллить
            }

            // Скроллим вниз
            ((JavascriptExecutor)driver).executeScript(
                    "window.scrollBy(0, arguments[0])", scrollStep);

            // Небольшая пауза между скроллами
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            attempts++;
        }

        throw new NoSuchElementException("Не удалось найти товар в разделе скидок после " + maxAttempts + " попыток скролла");
    }

    protected void enter(By locator) {
        WebElement input = waitForElement(locator);
        input.sendKeys(Keys.ENTER);
    }

    protected void type(By locator, String text) {
        WebElement input = waitForElement(locator);
        for (char c : text.toCharArray()) {
            input.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(50 + (long)(Math.random() * 100));
            } catch (InterruptedException ignored) {}
        }
        enter(locator);
    }


    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
