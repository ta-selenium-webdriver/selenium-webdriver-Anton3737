package com.softserve.academy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenCityNegativeRegistrationTest {
    private static WebDriver driver;

    @BeforeAll
    static void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Check if we are running in CI (GitHub Actions)
        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }
        
        driver = WebDriverManager.chromedriver().capabilities(options).create();
        driver.manage().window().maximize();
        // At this stage, we are not using complex waits, so we just maximize the f
        // At this stage, we are not using complex waits, so we just maximize the f
        // At this stage, we are not using complex waits, so we just maximize the f
        // At this stage, we are not using complex waits, so we just maximize the f
        // At this stage, we are not using complex waits, so we just maximize the f
        // At this stage, we are not using complex waits, so we just maximize the fff
    }

    @BeforeEach
    void openRegistrationForm() throws InterruptedException {
        // 1. Open the main page
        driver.navigate().to("https://www.greencity.cx.ua/#/greenCity");
        
        // Bad practice: using a delay to allow the page to load completely.
        // This is necessary because the site may load slowly.
        Thread.sleep(5000);

        // 2. Click the "Sign Up" button to open the modal window
        driver.findElement(By.cssSelector(".header_sign-up-btn > span")).click();

        // Bad practice: using a delay to allow the modal window to open.
        Thread.sleep(2000);
    }

    // --- TESTS ---

    @Test
    @DisplayName("Invalid email format (without @) → email error")
    void shouldShowErrorForInvalidEmail() throws InterruptedException {
        // One test = one reason for failure. Other fields must be valid.
        typeEmail("invalid-email");
        typeUsername("ValidUsername");
        typePassword("ValidPass123!");
        typeConfirm("ValidPass123!");

        // Give the system some time to validate and display the error
        Thread.sleep(1000);

        // Check that the error for email appeared
        assertEmailErrorVisible();
        // Check that the registration button is disabled (or registration did not occur)
        assertSignUpButtonDisabled();
    }

    @Test
    @DisplayName("All fields empty → required errors shown")
    void shouldShowErrorsForAllEmptyFields() throws InterruptedException {
        // TODO:
        // 1. Click each field or try to click Sign Up
        // 2. Check assertEmailErrorVisible(), assertUsernameErrorVisible(), etc.
    }

    @Test
    @DisplayName("Empty username → username required")
    void shouldShowErrorForEmptyUsername() throws InterruptedException {
        // TODO:
        // 1. Enter valid email and passwords
        // 2. Leave username empty (or click and leave)
        // 3. assertUsernameErrorVisible()
    }

    @Test
    @DisplayName("Short password (<8) → password rule error")
    void shouldShowErrorForShortPassword() throws InterruptedException {
        // TODO:
        // Enter a password like "123" and check for the error
    }

    @Test
    @DisplayName("Password with space → password rule error")
    void shouldShowErrorForPasswordWithSpace() throws InterruptedException {
        // Check a specific password validation rule
    }

    @Test
    @DisplayName("Confirm password mismatch → confirm error")
    void shouldShowErrorForPasswordMismatch() throws InterruptedException {
        // Enter different passwords in the Password and Confirm Password fields
    }

    // --- HELPERS (Helper methods) ---
    // This is the first step towards structuring code before learning Page Object

    private void typeEmail(String value) {
        WebElement field = driver.findElement(By.id("email"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeUsername(String value) {
        WebElement field = driver.findElement(By.id("firstName"));
        field.clear();
        field.sendKeys(value);
    }

    private void typePassword(String value) {
        WebElement field = driver.findElement(By.id("password"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeConfirm(String value) {
        WebElement field = driver.findElement(By.id("repeatPassword"));
        field.clear();
        field.sendKeys(value);
    }

    private void clickSignUp() {
        driver.findElement(By.cssSelector("button[type='submit'].greenStyle")).click();
    }

    private void assertEmailErrorVisible() {
        WebElement error = driver.findElement(By.id("email-err-msg"));
        assertTrue(error.isDisplayed(), "Email error message should be visible");
        // contains("required") or other text to avoid dependency on the full phrase
        assertTrue(error.getText().toLowerCase().contains("check") || error.getText().toLowerCase().contains("correctly"));
    }

    private void assertUsernameErrorVisible() {
        // Find the error element for the username (id may differ, check on the site)
        // For example: driver.findElement(By.xpath("//input[@id='firstName']/following-sibling::div"))
    }

    private void assertSignUpButtonDisabled() {
        WebElement btn = driver.findElement(By.cssSelector("button[type='submit'].greenStyle"));
        assertFalse(btn.isEnabled(), "The 'Sign Up' button should be disabled with invalid data");
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
