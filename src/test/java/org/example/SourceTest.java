package org.example;

import org.example.driver.WebDriverFactory;
import org.example.model.BrowserType;
import org.example.pages.CartPage;
import org.example.pages.MainPage;
import org.example.pages.ProductPage;
import org.example.pages.SearchPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class SourceTest {

    private WebDriver driver;

    @ParameterizedTest
    @DisplayName("Кейс 0 - Загрузка главной страницы")
    @EnumSource(BrowserType.class)
    public void homePageOpenedTest(BrowserType browser) {
        driver = WebDriverFactory.getDriver(browser);

        MainPage mainPage = new MainPage(driver);
        mainPage.openMainPage();

        assertTrue(mainPage.isPageLoaded(), "MainPage should be loaded");
    }

    @ParameterizedTest
    @DisplayName("Кейс 1 - Поиск товара")
    @EnumSource(BrowserType.class)
    public void searchTextTest(BrowserType browser) {
        driver = WebDriverFactory.getDriver(browser);

        MainPage mainPage = new MainPage(driver);
        mainPage.openMainPage();
        assertTrue(mainPage.isPageLoaded(), "MainPage should be loaded");

        final String search = "Tom Ford";
        SearchPage searchPage = mainPage.searchFor(search);
        assertTrue(searchPage.isPageLoaded(), "SearchPage should be loaded");

        assertTrue(searchPage.isSearchCorrect(search), "SearchPage has no relevant search result");
    }

    @ParameterizedTest
    @DisplayName("Кейс 2 - Добавление товара в корзину из раздела скидок")
    @EnumSource(BrowserType.class)
    public void addToCartTest(BrowserType browser) {
        driver = WebDriverFactory.getDriver(browser);

        MainPage mainPage = new MainPage(driver);
        mainPage.openMainPage();
        assertTrue(mainPage.isPageLoaded(), "Main page should load");

        // Добавляем товар из раздела скидок
        ProductPage productPage = mainPage.addItemToCartFromDiscounts();
        assertTrue(productPage.isPageLoaded(), "Product page should load");

        // Проверяем что корзина пуста перед добавлением
        assertFalse(productPage.isItemInCart(), "Cart should be empty initially");

        // Добавляем товар в корзину
        productPage.addToCart();

        // Проверяем что счетчик корзины изменился
        assertTrue(productPage.isItemInCart(), "Cart should contain 1 item after adding");

        assertEquals(1, productPage.cartValue(), "Cart count should be '1'");
    }


    @ParameterizedTest
    @DisplayName("Кейс 3 - Увеличение количества товара в корзине")
    @EnumSource(BrowserType.class)
    public void cartTotalPriceUpdateTest(BrowserType browser) {
        driver = WebDriverFactory.getDriver(browser);

        // 1. Добавляем товар в корзину
        MainPage mainPage = new MainPage(driver);
        mainPage.openMainPage();
        ProductPage productPage = mainPage.addItemToCartFromDiscounts();
        productPage.addToCart();

        // 2. Переходим в корзину
        CartPage cartPage = productPage.goToCart();
        assertTrue(cartPage.isPageLoaded(), "Cart page should load");

        // 3. Получаем начальные значения
        double initialTotal = cartPage.getTotalPrice();
        double initialSubtotal = cartPage.getItemsSubtotal();
        int initialQuantity = cartPage.getCurrentQuantity();

        // 4. Увеличиваем количество товара
        cartPage.increaseQuantity();

        // 5. Проверяем изменения
        int newQuantity = cartPage.getCurrentQuantity();
        double newTotal = cartPage.getTotalPrice();
        double newSubtotal = cartPage.getItemsSubtotal();

        // Проверки
        assertEquals(initialQuantity + 1, newQuantity, "Quantity should increase by 1");
        assertEquals(initialSubtotal * 2, newSubtotal, 0.01, "Subtotal should double");
        assertEquals(initialTotal * 2, newTotal, 0.01, "Total price should double");
    }

    @ParameterizedTest
    @DisplayName("Кейс 4 - Проверка сортировки цены по возрастанию")
    @EnumSource(BrowserType.class)
    public void searchTextWithPriceSortAscTest(BrowserType browser) {
        driver = WebDriverFactory.getDriver(browser);

        MainPage mainPage = new MainPage(driver);
        mainPage.openMainPage();
        assertTrue(mainPage.isPageLoaded(), "MainPage should be loaded");

        final String search = "Tom Ford";
        SearchPage searchPage = mainPage.searchFor(search);
        assertTrue(searchPage.isPageLoaded(), "SearchPage should be loaded");

        assertTrue(searchPage.isSearchCorrect(search), "SearchPage has no relevant search result");

        // Проверка сортировки по возрастанию цены (второй элемент в списке)
        searchPage.selectSortByPrice(true);
        assertTrue(searchPage.isPriceSortedAscending(), "Prices should be sorted in ascending order");
    }

    @ParameterizedTest
    @DisplayName("Кейс 5 - Проверка сортировки цены по уменьшению")
    @EnumSource(BrowserType.class)
    public void searchTextWithPriceSortDescTest(BrowserType browser) {
        driver = WebDriverFactory.getDriver(browser);

        MainPage mainPage = new MainPage(driver);
        mainPage.openMainPage();
        assertTrue(mainPage.isPageLoaded(), "MainPage should be loaded");

        final String search = "Tom Ford";
        SearchPage searchPage = mainPage.searchFor(search);
        assertTrue(searchPage.isPageLoaded(), "SearchPage should be loaded");

        assertTrue(searchPage.isSearchCorrect(search), "SearchPage has no relevant search result");

        // Проверка сортировки по уменьшению цены (второй элемент в списке)
        searchPage.selectSortByPrice(false);
        assertTrue(searchPage.isPriceSortedDescending(), "Prices should be sorted in descending order");
    }

    @ParameterizedTest
    @DisplayName("Кейс 6 - Проверка фильтра по минимальной цене")
    @EnumSource(BrowserType.class)
    public void testMinPriceFilterForTomFord(BrowserType browser) throws InterruptedException {
        driver = WebDriverFactory.getDriver(browser);

        MainPage mainPage = new MainPage(driver);
        mainPage.openMainPage();
        assertTrue(mainPage.isPageLoaded(), "MainPage should be loaded");

        final String search = "Tom Ford";
        SearchPage searchPage = mainPage.searchFor(search);
        assertTrue(searchPage.isPageLoaded(), "SearchPage should be loaded");
        assertTrue(searchPage.isSearchCorrect(search), "Search results are incorrect");

        // Устанавливаем минимальную цену и применяем фильтр
        int minPrice = 5000;
        searchPage.setMinPrice(minPrice);


        // Сортируем по возрастанию цены
        searchPage.selectSortByPrice(true);

        // Проверяем что цена первого элемента >= minPrice
        int firstItemPrice = searchPage.getFirstItemPrice();
        assertTrue(firstItemPrice >= minPrice,
                String.format("Цена первого элемента (%d) меньше минимальной (%d)", firstItemPrice, minPrice));
    }

    @ParameterizedTest
    @DisplayName("Кейс 7 - Проверка фильтра по максимальной цене")
    @EnumSource(BrowserType.class)
    public void testMaxPriceFilterForTomFord(BrowserType browser) {
        driver = WebDriverFactory.getDriver(browser);

        // 1. Выполняем поиск Tom Ford
        MainPage mainPage = new MainPage(driver);
        mainPage.openMainPage();
        assertTrue(mainPage.isPageLoaded(), "MainPage should be loaded");

        final String search = "Tom Ford";
        SearchPage searchPage = mainPage.searchFor(search);
        assertTrue(searchPage.isPageLoaded(), "SearchPage should be loaded");
        assertTrue(searchPage.isSearchCorrect(search), "Search results are incorrect");

        // 2. Устанавливаем максимальную цену и применяем фильтр
        int maxPrice = 15000; // Пример максимальной цены
        searchPage.setMaxPrice(maxPrice);

        // 3. Сортируем по убыванию цены
        searchPage.selectSortByPrice(false);

        // 4. Проверяем что цена первого элемента <= maxPrice
        int firstItemPrice = searchPage.getFirstItemPrice();
        assertTrue(firstItemPrice <= maxPrice,
                String.format("Цена первого элемента (%d) больше максимальной (%d)", firstItemPrice, maxPrice));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
