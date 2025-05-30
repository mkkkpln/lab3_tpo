package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends Page {
    // Локаторы
    private final By cartItem = By.xpath("//div[contains(@class, 'item')]");
    private final By itemQuantity = By.xpath("//rd-counter/div[2]");
    private final By increaseQuantityButton = By.xpath("//rd-counter/div[3]");
    private final By totalPrice = By.xpath("//cart-totals-result//div[contains(@class, 'total')]/span[2]"); // Итоговая цена
    private final By itemsSubtotal = By.xpath("//cart-totals-result//div[contains(@class, 'row')][1]/span[2]"); // Сумма товаров без скидки

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementDisplayed(cartItem) && isElementDisplayed(totalPrice);
    }

    public double getTotalPrice() {
        String priceText = waitForElement(totalPrice).getText()
            .replaceAll("[^\\d,]", "")
            .replace(',', '.');
        return Double.parseDouble(priceText);
    }

    public double getItemsSubtotal() {
        String priceText = waitForElement(itemsSubtotal).getText()
            .replaceAll("[^\\d,]", "")
            .replace(',', '.');
        return Double.parseDouble(priceText);
    }

    public int getCurrentQuantity() {
        return Integer.parseInt(waitForElement(itemQuantity).getText());
    }

    public void increaseQuantity() {
        // 1. Получаем текущее количество перед изменением
        int initialQuantity = getCurrentQuantity();

        // 2. Кликаем на кнопку увеличения
        click(increaseQuantityButton);

        // 3. Ждем точного увеличения количества на 1
        wait.until(driver -> {
            int currentQuantity = getCurrentQuantity();
            return currentQuantity == initialQuantity + 1;
        });

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}