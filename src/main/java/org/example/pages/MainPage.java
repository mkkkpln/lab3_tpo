package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class MainPage extends Page {


    private final By catalogButton = By.xpath("//a[contains(@class, 'ng-star-inserted') and @href='#' and text()=' Каталог ']");
    private final By navigationContainer = By.xpath("//div[@cms-slot and @position='HomeSection3']");
    private final By searchContainer = By.xpath("//div[@cms-slot and @position='Search']");
    private final By searchInput = By.xpath("//input[@type='text' and @placeholder='Поиск в РИВ ГОШ']");
    private final By searchButton = By.xpath("//button[@skin='simple' and normalize-space()='Найти']");
    private final By discountItemsContainer = By.xpath("//div[contains(@class, 'discount-items-section')]");
    private final By firstDiscountItem = By.xpath("//product-item[contains(@class, 'ng-star-inserted')]");
    private final String PAGE_URL = "https://rivegauche.ru/";

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public MainPage openMainPage() {
        driver.get(PAGE_URL);
        return this;
    }

    @Override
    public boolean isPageLoaded() {
        return isElementDisplayed(catalogButton)
                && isElementDisplayed(searchContainer)
                && isElementDisplayed(navigationContainer);
    }

    public SearchPage searchFor(String text) {
        click(searchContainer);
        type(searchInput, text);
        if (isSearchButtonActive()) {
            click(searchButton);
        } else {
            enter(searchInput);
        }
        return new SearchPage(driver);
    }

    public ProductPage addItemToCartFromDiscounts() {
        scrollUntilFindElement(firstDiscountItem);
        click(firstDiscountItem);
        return new ProductPage(driver);
    }

    private boolean isSearchButtonActive() {
        List<WebElement> searchButtons = driver.findElements(searchButton);
        return !searchButtons.isEmpty() && searchButtons.get(0).isDisplayed();
    }

}