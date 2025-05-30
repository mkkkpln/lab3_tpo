package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

public class SearchPage extends Page {
    private final String PAGE_URL = "https://rivegauche.ru/search?text=tom%20ford";
    private final By resultsCountContainer = By.xpath("//span[contains(@class, 'product-finder') and contains(text(), 'Найдено товаров:')]");
    private final By sortingResultContainer = By.xpath("//div[contains(@class, 'search-sorting') and contains(@class, 'ng-tns-')]");
    private final By applyFilterButton = By.xpath("//button[normalize-space()='Найти']");
    private final By minPriceInput = By.xpath("//input[@aria-label='min']");
    private final By maxPriceInput = By.xpath("//input[@aria-label='max']");
    private final By firstItemPrice = By.xpath("(//product-item-price//div[contains(@class, 'from-price')])[1]");
    private final By pricesList = By.xpath("//*[contains(@class, 'price')]");
    private final By sortingMenu = By.xpath("//ul[contains(@_ngcontent-ng-c891184530, '')]");
    private final By sortingButton = By.xpath("//div[contains(@_ngcontent-ng-c3206282739, '') and contains(text(), 'Сортировать')]");

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementDisplayed(resultsCountContainer)
                && isElementDisplayed(sortingResultContainer);
    }

    public boolean isSearchCorrect(String searchedText) {
        try {
            // Проверяем что URL содержит поисковый запрос
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            if (!currentUrl.contains(searchedText.toLowerCase().replace(" ", "%20"))) {
                return false;
            }

            // Проверяем наличие хотя бы одного товара
            By productLink = By.xpath(
                    "//a[contains(@class, 'name') and " +
                    "contains(translate(@href, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" +
                    searchedText.toLowerCase().replace(" ", "-") + "')]"
            );

            return wait.until(ExpectedConditions
                            .presenceOfAllElementsLocatedBy(productLink))
                           .size() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    private void openSortDropdown() {
        // Наводим курсор на элемент "Сортировать" (div с текстом "Сортировать")
        WebElement sortTrigger = driver.findElement(sortingButton);

        Actions actions = new Actions(driver);
        actions.moveToElement(sortTrigger).perform();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectSortByPrice(boolean ascending) {
        // Открываем выпадающее меню
        openSortDropdown();

        // Формируем XPath в зависимости от направления сортировки
        String sortXpath = ascending
                ? "(//li[contains(., 'По цене')])[1]"
                : "(//li[contains(., 'По цене')])[2]";

        // Ждём и кликаем по нужному варианту
        click(By.xpath(sortXpath));

        // Ждём закрытия меню
        wait.until(ExpectedConditions.invisibilityOfElementLocated(sortingMenu));

    }


    public boolean isPriceSortedAscending() {
        return isPriceSorted(true);
    }

    public boolean isPriceSortedDescending() {
        return isPriceSorted(false);
    }

    private boolean isPriceSorted(boolean ascending) {
        List<WebElement> priceElements = driver.findElements(pricesList);
        List<Double> prices = priceElements.stream()
                .map(e -> {
                    String text = e.getText().replaceAll("[^0-9.]", "");
                    return text.isEmpty() ? 0.0 : Double.parseDouble(text);
                })
                .collect(Collectors.toList());

        for (int i = 0; i < prices.size() - 1; i++) {
            if (ascending && prices.get(i) > prices.get(i + 1)) {
                return false;
            }
            if (!ascending && prices.get(i) < prices.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    public void setMinPrice(int price) {
        WebElement minInput = driver.findElement(minPriceInput);
        minInput.clear();
        type(minPriceInput, String.valueOf(price));
    }

    public void setMaxPrice(int price) {
        WebElement maxInput = driver.findElement(maxPriceInput);
        maxInput.clear();
        type(maxPriceInput, String.valueOf(price));
    }


    public int getFirstItemPrice() {
        WebElement firstItemPriceElement = wait
                .until(ExpectedConditions.visibilityOfElementLocated(firstItemPrice));
        return parsePrice(firstItemPriceElement.getText());
    }

    private int parsePrice(String priceText) {
        return Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
    }


}
