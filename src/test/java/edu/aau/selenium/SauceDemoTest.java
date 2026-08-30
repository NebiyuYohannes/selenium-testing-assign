package edu.aau.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SauceDemoTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;

    // T8: Test lifecycle
    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        // Add options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
        loginPage = new LoginPage(driver); // T7: Page Object
        inventoryPage = new InventoryPage(driver);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // T8: Test lifecycle
        }
    }

    // T1: Navigation smoke test
    @Test
    public void testPageLoad() {
        loginPage.open();
        assertEquals("Swag Labs", driver.getTitle(), "Title should match the expected one");
    }

    // T3: The positive path
    @Test
    public void testPositiveLoginAndAddToCart() {
        loginPage.open();
        loginPage.login("standard_user", "secret_sauce");
        
        // Assert we reached inventory page
        assertTrue(inventoryPage.isLoaded(), "Inventory page should be visible after login");
        
        inventoryPage.addFirstItemToCart();
        assertEquals("1", inventoryPage.getCartItemCount(), "Cart should have 1 item");
    }

    // T4: A negative path
    @Test
    public void testNegativeLogin() {
        loginPage.open();
        loginPage.login("invalid_user", "wrong_password");
        
        // Assert error message
        String errorMessage = loginPage.getErrorMessage(); // Uses Explicit wait (T5)
        assertTrue(errorMessage.contains("Username and password do not match"), "Error message should indicate invalid credentials");
    }

    // T6: A data-driven test
    // Equivalence Partitioning:
    // Partition 1: Valid user -> login success
    // Partition 2: Locked out user -> specific error message
    // Partition 3: Invalid user -> generic error message
    @ParameterizedTest
    @CsvSource({
            "standard_user, secret_sauce, 'success'",
            "locked_out_user, secret_sauce, 'Epic sadface: Sorry, this user has been locked out.'",
            "invalid_user, wrong_pass, 'Epic sadface: Username and password do not match any user in this service'"
    })
    public void testLoginPartitions(String username, String password, String expectedOutcome) {
        loginPage.open();
        loginPage.login(username, password);

        if ("success".equals(expectedOutcome)) {
            assertTrue(inventoryPage.isLoaded(), "Should successfully login and load inventory page");
        } else {
            String actualError = loginPage.getErrorMessage();
            assertEquals(expectedOutcome, actualError, "Error message should match for partition");
        }
    }
}
