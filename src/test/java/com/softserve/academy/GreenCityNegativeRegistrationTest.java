package com.softserve.academy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenCityNegativeRegistrationTest {

    private static WebDriver driver;

    @BeforeAll
    static void setUp() {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("intl.accept_languages", "en-GB, en");
        // Check if we are running in CI (GitHub Actions)
        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        driver = WebDriverManager.firefoxdriver().capabilities(options).create();
        driver.manage().window().maximize();
    }

    @BeforeEach
    void openRegistrationForm() throws InterruptedException {
        // 1. Open the main page
        driver.navigate().to("https://www.greencity.cx.ua/#/greenCity");

        // Bad practice: using a delay to allow the page to load completely.
        // This is necessary because the site may load slowly.
        Thread.sleep(1000);

        // 2. Click the "Sign Up" button to open the modal window
        driver.findElement(By.cssSelector(".header_sign-up-btn > span")).click();

        // Bad practice: using a delay to allow the modal window to open.
        Thread.sleep(1000);
    }


    @AfterEach
    void cleanUp() {
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
    }

    // --- TESTS ---


    @Test
    @DisplayName("Invalid email format (without @) → email error")
    void shouldShowErrorForInvalidEmail() throws InterruptedException {
        // One test = one reason for failure. Other fields must be valid.
        typeEmail("BarashASUAgmail.com");
        typeUsername("Anton");
        typePassword("TestAdmin2025!");
        typeConfirm("TestAdmin2025!");

        // Give the system some time to validate and display the error
        Thread.sleep(2000);

        WebElement msgErr = driver.findElement(By.id("email-err-msg"));
        String actualError = msgErr.getText();

        System.out.println("actualError = " + actualError);
        String expectedErrorMsg = ErrorsMsgEN.EMAIL_INVALID;
        System.out.println(expectedErrorMsg);

        assertTrue(expectedErrorMsg.contains(actualError));

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
        typeEmail(" ");
        typeUsername(" ");
        typePassword(" ");
        typeConfirm("");


        Thread.sleep(4000);

        assertEmailErrorVisible();
        assertUsernameErrorVisible();
        assertPasswordWithSpaceAndNoSymbolErrorVisible();
        assertConfirmErrorVisible();
        assertSignUpButtonDisabled();
        assertSignUpButtonDisabled();

    }

    @Test
    @DisplayName("Empty username → username required")
    void shouldShowErrorForEmptyUsername() throws InterruptedException {
        // TODO:
        // 1. Enter valid email and passwords
        // 2. Leave username empty (or click and leave)
        // 3. assertUsernameErrorVisible()
        typeEmail("BarashASUA@gmail.com");
        typeUsername("");
        typePassword("TestAdmin2025!");
        typeConfirm("TestAdmin2025");

        assertUsernameErrorVisible();
        assertUsernameErrorVisible();
    }

    @Test
    @DisplayName("Short password (<8) → password rule error")
    void shouldShowErrorForShortPassword() throws InterruptedException {
        // TODO:
        // Enter a password like "123" and check for the error

        typeEmail("BarashASUA@gmail.com");
        typeUsername("Anton");
        typePassword("test");

        Thread.sleep(5000);

        WebElement msgErr = driver.findElement(By.id("password-err-msg"));
        String actualError = msgErr.getText();
        String expectedErrorMsg = ErrorsMsgEN.PASSWORD_INVALID_RULES;


        assertTrue(expectedErrorMsg.contains(actualError));
    }

    @Test
    @DisplayName("Password with space → password rule error")
    void shouldShowErrorForPasswordWithSpace() throws InterruptedException {
        typeEmail("BarashASUA@gmail.com");
        typeUsername("Anton");
        typePassword(" ");

        Thread.sleep(5000);

        WebElement msgErr = driver.findElement(By.className("password-not-valid"));
        String actualError = msgErr.getText();
        System.out.println(actualError);
        String expectedErrorMsg = ErrorsMsgEN.PASSWORD_INVALID_RULES;
        System.out.println(expectedErrorMsg);

        assertPasswordWithSpaceAndNoSymbolErrorVisible();
        assertSignUpButtonDisabled();

        assertTrue(expectedErrorMsg.contains(actualError));
    }

    @Test
    @DisplayName("Confirm password mismatch → confirm error")
    void shouldShowErrorForPasswordMismatch() throws InterruptedException {
        // Enter different passwords in the Password and Confirm Password fields
        typeEmail("BarashASUA@gmail.com");
        typeUsername("Anton");
        typePassword("TestAdmin2025!");
        typeConfirm("TestAdmin2030!!");


        WebElement msgErr = driver.findElement(By.id("confirm-err-msg"));
        String actualError = msgErr.getText();
        System.out.println(actualError);
        String expectedErrorMsg = ErrorsMsgEN.CONFIRM_MISMATCH;
        System.out.println(expectedErrorMsg);
        assertTrue(expectedErrorMsg.contains(actualError));

        assertConfirmErrorVisible();
        assertSignUpButtonDisabled();
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
        assertTrue(error.getText().toLowerCase().contains("check") || error.getText().toLowerCase().contains("required"));
        System.out.println("The Email error message is visible");
    }


    private void assertPasswordWithSpaceAndNoSymbolErrorVisible() {
        WebElement error = driver.findElement(By.className("password-not-valid"));
        assertTrue(error.isDisplayed(), "Password error message should be visible");
        assertTrue(error.getText().toLowerCase().contains("uppercase") || error.getText().toLowerCase().contains("lowercase"));
        System.out.println("The password error message is visible");
    }

    private void assertUsernameErrorVisible() {
        // Find the error element for the username (id may differ, check on the site)
        // For example: driver.findElement(By.xpath("//input[@id='firstName']/following-sibling::div"))

        WebElement error = driver.findElement(By.id("firstname-err-msg"));
        assertTrue(error.isDisplayed(), "Email error message should be visible");
        assertTrue(error.getText().toLowerCase().contains("check") || error.getText().toLowerCase().contains("required"));
        System.out.println("The username error message is visible");
    }

    private void assertSignUpButtonDisabled() {
        WebElement btn = driver.findElement(By.cssSelector("button[type='submit'].greenStyle"));
        assertFalse(btn.isEnabled(), "The 'Sign Up' button should be disabled with invalid data");
        System.out.println("The 'Sign Up' button is disabled with invalid data");
    }

    private void assertConfirmErrorVisible() {
        WebElement error = driver.findElement(By.id("confirm-err-msg"));
        assertTrue(error.isDisplayed(), "Confirm password error message should be visible");
        assertTrue(error.getText().toLowerCase().contains("do not match") || error.getText().toLowerCase().contains("required"));
        System.out.println("The error message is visible");
    }


    @AfterAll
    static void tearDown() {
        if (driver != null) {
//            driver.quit();
            driver.navigate().refresh();
        }
    }

}
