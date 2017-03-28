package com.mycompany.seliniumtest;

import org.junit.After;
import org.junit.Before; 
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement; 
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.openqa.selenium.firefox.FirefoxDriver;

public class localTest {

    WebDriver driver;
    private int waitTime = 5;

    @Before
    public void setupTests () { 
        System.setProperty("webdriver.gecko.driver","/usr/local/Cellar/geckodriver/geckodriver");
 
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset"); 
        
        driver = new FirefoxDriver(); 
        driver.get("http://localhost:3000"); 
    }

    @After
    public void endTests () { 
        driver.quit();
    }

    @Test
    @DisplayName("should verify that data loaded and the DOM is constructed")
    public void testIfDomIsConstructed () {
        final int expectedTableSize = 5;
        new WebDriverWait(driver, waitTime).until(new Function<WebDriver, Boolean>() {
            public Boolean apply (WebDriver wd) {
                int actualTableSize = wd.findElement(By.id("tbodycars"))
                                        .findElements(By.tagName("tr"))
                                        .size();
                assertThat(actualTableSize, is(expectedTableSize));

                return true;
            }
        });
    }

    @Test
    @DisplayName("should write 2002 in input filter and verify that there will only be two rows by then, and again five when cleared")
    public void testInputFilterAndClear () {

        final int expectedTableSizeTwo = 2;
        final int expectedTableSizeFive = 5;

        new WebDriverWait(driver, waitTime).until(new Function<WebDriver, Object>() {

            public Boolean apply (WebDriver wd) {
                wd.findElement(By.id("filter"));
                return true;
            }
         });

        driver.findElement(By.id("filter")).sendKeys("2002");

        int actualTableSizeTwo = driver.findElement(By.id("tbodycars"))
                .findElements(By.tagName("tr"))
                .size();


        driver.findElement(By.id("filter")).sendKeys(Keys.CONTROL + "a");
        driver.findElement(By.id("filter")).sendKeys(Keys.DELETE);

        int actualTableSizeFive = driver.findElement(By.id("tbodycars"))
                .findElements(By.tagName("tr"))
                .size();

        assertThat(actualTableSizeTwo, is(expectedTableSizeTwo));
        assertThat(actualTableSizeFive, is(expectedTableSizeFive));
    }


    @Test
    @DisplayName("should click on sort button for year, and verify that the top element is the car with id 938 and bottom one is 940")
    public void testSortButtonYear () {
        final String expectedFirstId = "938";
        final String expectedBottomId = "940";

        new WebDriverWait(driver, waitTime).until(new Function<WebDriver, Object>() {
            public Boolean apply (WebDriver wd) {
                wd.findElement(By.id("tbodycars"));
                return true;
            }

        });

        driver.findElement(By.id("h_year")).click();

        List<WebElement> tableRows = driver.findElement(By.id("tbodycars"))
                .findElements(By.tagName("tr"));

        String actualFirstId = tableRows.get(0)
                .findElements(By.tagName("td"))
                .get(0)
                .getText();

        String actualBottomId = tableRows.get(tableRows.size() - 1)
                .findElements(By.tagName("td"))
                .get(0)
                .getText();

        assertThat(actualFirstId, is(expectedFirstId));
        assertThat(actualBottomId, is(expectedBottomId));
    }

    @Test
    @DisplayName("Should change description of car with id 938")
    public void testEditButton () {

        final String expectedDescription = "Cool car";
        new WebDriverWait(driver, waitTime).until(new Function<WebDriver, Object>() {
            public Boolean apply (WebDriver wd) {
                wd.findElement(By.id("tbodycars"));
                return true;
            }
        });

        List<WebElement> rows = driver.findElement(By.id("tbodycars"))
                .findElements(By.tagName("tr"));

        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));

            if(columns.get(0).getText().equals("938")) {

                columns.get(columns.size() - 1) // Get last td in that tr - which should be: "action"/description
                        .findElements(By.tagName("a"))
                        .get(0) // First a tag --> description, not delete
                        .click();

                WebElement descriptionInput = driver.findElement(By.id("description"));
                descriptionInput.clear();
                descriptionInput.sendKeys("Cool car");

                driver.findElement(By.id("save")).click();

                String actualDescription = columns.get(5).getText();

                assertThat(actualDescription, is(expectedDescription));
                break;
            }
        }
    }

    @Test
    public void testNewCarButtonErrorMessage () {
        final String expectedErrorMessage = "All fields are required";

        new WebDriverWait(driver, waitTime).until(new Function<WebDriver, Object>() {
            public Boolean apply (WebDriver wd) {

                wd.findElement(By.id("new")).click();

                wd.findElement(By.id("save")).click();

                String actualErrorMessage = wd.findElement(By.id("submiterr")).getText();

                assertThat(actualErrorMessage, is(expectedErrorMessage));
                return true;
            }
        });
    }

    @Test
    public void testNewCarButton () {
        final int expectedTableSize = 6;

        new WebDriverWait(driver, waitTime).until(new Function<WebDriver, Object>() {
            public Boolean apply (WebDriver wd) {
                wd.findElement(By.id("year"));
                return true;
            }
        });


        driver.findElement(By.id("year")).sendKeys("2008");
        driver.findElement(By.id("registered")).sendKeys("2002-5-5");
        driver.findElement(By.id("make")).sendKeys("Kia");
        driver.findElement(By.id("model")).sendKeys("Rio");
        driver.findElement(By.id("description")).sendKeys("As new");
        driver.findElement(By.id("price")).sendKeys("31000"); 
        driver.findElement(By.id("save")).click();

        int actualTableSize = driver.findElement(By.id("tbodycars"))
                .findElements(By.tagName("tr"))
                .size();
        assertThat(actualTableSize, is(expectedTableSize));



    }

}
