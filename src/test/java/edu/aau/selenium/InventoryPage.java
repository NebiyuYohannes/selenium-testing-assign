package edu.aau.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class InventoryPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By inventoryContainer = By.id("inventory_container");
    private By firstItemAddButton = By.cssSelector(".inventory_item:first-child button");
    private By cartBadge = By.className("shopping_cart_badge");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void addFirstItemToCart() {
        driver.findElement(firstItemAddButton).click();
    }

    public String getCartItemCount() {
        return driver.findElement(cartBadge).getText();
    }
}
