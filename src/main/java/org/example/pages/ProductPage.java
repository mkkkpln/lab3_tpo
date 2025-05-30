package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductPage extends Page {
    private final By addToCartButton = By.xpath("//span[contains(@class, 'ctrl') and contains(text(), 'В корзину')]");
    private final By goToCartButton = By.xpath("//mini-cart[contains(@class, 'ng-star-inserted')]");
    private final By miniCartCount = By.xpath("//mini-cart//span[contains(@class, 'count')]");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementDisplayed(addToCartButton);
    }

    public CartPage goToCart() {
        click(goToCartButton);
        return new CartPage(driver);
    }

    public void addToCart() {
        click(addToCartButton);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(miniCartCount, "1"));
    }

    public Integer cartValue() {
        WebElement cartCount = driver.findElement(miniCartCount);
        return Integer.parseInt(cartCount.getText());
    }

    public boolean isItemInCart() {
        try {
            return driver.findElement(miniCartCount).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}