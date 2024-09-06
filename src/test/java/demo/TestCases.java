package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.logging.Level;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;
    

    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */

     
    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment
     */

    //Iterate through the table and collect the Team Name, Year and Win % for the teams with Win % less than 40% (0.40
    @Test
    public void testCase01(){
        System.out.println("Start Test Case: testCase01");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        List<HashMap<String,Object>> list = new ArrayList<>();
        driver.get("https://www.scrapethissite.com/pages/");
        WebElement hockeyTeams = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Hockey Teams: Forms, Searching and Pagination']")));
        Wrappers.clickOn(hockeyTeams,driver);
       
        
    
        for(int i=1;i<=4;i++){
            List<WebElement> winPercentages = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table//th[contains(text(),'Win %')]//ancestor::tbody/tr/td[contains(@class,'pct text-danger')]")));
            for(WebElement elem: winPercentages){
               
                Double checkPercent = Double.parseDouble(elem.getText());
                if(checkPercent<0.40){
                    HashMap<String,Object> map = new HashMap<>();
                    String teamName = elem.findElement(By.xpath(".//preceding-sibling::td[@class='name']")).getText();
                    //System.out.println(teamName);
                    String year = elem.findElement(By.xpath(".//preceding-sibling::td[@class='year']")).getText();
                    String percehtText = elem.getText();

                    long epochTime = System.currentTimeMillis()/1000;
                    String epoch = String.valueOf(epochTime);

                    map.put("Epoch", epoch);
                    map.put("TeamName",teamName);
                    map.put("Year", year);
                    map.put("Win%", percehtText);
                    list.add(map);
                }
                
            }
            WebElement nextPage = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@aria-label='Next']")));
                Wrappers.clickOn(nextPage, driver);
           
        }

        ObjectMapper mapper =  new ObjectMapper();

       
 
        String userDir = System.getProperty("user.dir");
 
        //Writing JSON on a file
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(userDir + "\\src\\test\\resources\\hockey-team-data.json"), list);
        } catch (IOException e) {
            e.printStackTrace();
        }


      
        System.out.println("End Test Case: testCase01");
    }

    @Test
    public void testCase02(){
        System.out.println("Start Test Case: testCase02");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        List<HashMap<String,String>> list = new ArrayList<>();
        driver.get("https://www.scrapethissite.com/pages/");
        WebElement oscars = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Oscar Winning Films: AJAX and Javascript']")));
        Wrappers.clickOn(oscars, driver);
        
        List<WebElement> yearList= wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//a[@class='year-link']")));
        for(WebElement elem: yearList){
            elem.click();
            boolean isWinner=true;
            List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//tr[@class='film']"))); 
            String year= elem.getText();
            
            for(int i=0;i<rows.size();i++){
                long epoch = System.currentTimeMillis()/1000;
                String epochNow=String.valueOf(epoch);
                String title=rows.get(i).findElement(By.xpath("./td[@class='film-title']")).getText();
                String nominations=rows.get(i).findElement(By.xpath("./td[@class='film-nominations']")).getText();
                String awards = rows.get(i).findElement(By.xpath("./td[@class='film-awards']")).getText();
                String Winner = String.valueOf(isWinner);
                isWinner=false;

                HashMap<String,String> map = new HashMap<>();
                map.put("epoch", epochNow);
                map.put("Title", title);
                map.put("Nominations", nominations);
                map.put("Awards", awards);
                map.put("Winner", Winner);
                map.put("Year", year);
                list.add(map);
                if(i==4){
                    break;
                }
                
                
            }
        
    }

    ObjectMapper mapper = new ObjectMapper();
    String userDir = System.getProperty("user.dir");
 
        //Writing JSON on a file
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(userDir + "\\src\\test\\resources\\oscar-winner-data.json"), list);
        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println("End Test Case: testCase02");
    }
    
    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}