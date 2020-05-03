package com.eli.trading.stock.ticker;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Fidelity 
{
    private static final String fidelityLoginUrl = "https://www.fidelity.com";
    private static final String expectedFidelityLoginTitle = "Fidelity Investments";
    private final Scanner keyIn = new Scanner(System.in);

    public String getCodeFromKeyboardInput(String message) throws IOException {
        System.out.println(message);
        String entered = keyIn.nextLine();
        return entered;
    }
            
            
            
    public void doIt( String[] args ) throws IOException
    {
        System.setProperty("webdriver.gecko.driver", "C:\\\\geckodriver-v0.26.0-win64\\geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver,10);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        
        try {
            driver.get(fidelityLoginUrl);
            
            String title = driver.getTitle();

            if (!title.contains(expectedFidelityLoginTitle)) {
                driver.close();
                System.exit(1);
            }
            
            String selector = "input[id=userId-input]";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            WebElement userIdElem =driver.findElement(By.cssSelector(selector));
            userIdElem.sendKeys("elquetransporta");

            selector = "input[id=password]";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            WebElement passwordElem =driver.findElement(By.cssSelector(selector));
            passwordElem.sendKeys("Un@_P@_1nv3r$10n@r!!");

            selector = "button[id=fs-login-button]";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            WebElement loginButtonElem =driver.findElement(By.cssSelector(selector));
            loginButtonElem.click();

            selector = "div[id=step-challenge] button";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            WebElement nextToGetCodeButtonElem =driver.findElement(By.cssSelector(selector));
            nextToGetCodeButtonElem.click();

            selector = "label[id=channel-label-sms]";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            WebElement textMsgRadioButtonElem =driver.findElement(By.cssSelector(selector));
            textMsgRadioButtonElem.click();

            selector = "div[id=step-selectChannel] button";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            WebElement nextToGetTextMsgButtonElem =driver.findElement(By.cssSelector(selector));
            nextToGetTextMsgButtonElem.click();

            String code = getCodeFromKeyboardInput("ENTER code:");
            
            selector = "input[id=code]";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            WebElement codeElem = driver.findElement(By.cssSelector(selector));
            codeElem.sendKeys(code);

            String script = "document.querySelector('form[id=validateCodeForm] button').click();";
            js.executeScript(script);
            
            TimeUnit.SECONDS.sleep(5);
            
            script = "document.querySelectorAll('a[class=pnshl]')[0].click();";
            js.executeScript(script);
            
            TimeUnit.MILLISECONDS.sleep(2000);

            script = "document.querySelectorAll('span[class=pi-nav-nowrap]')[2].click();";
            js.executeScript(script);

            TimeUnit.SECONDS.sleep(6);

            String stockSymbol = args[0];

            script = "document.querySelectorAll('span[class=stock-symbol]').forEach((x)=>{ if (x.innerText == '"+stockSymbol+"') x.parentNode.parentNode.click();});";
            js.executeScript(script);
            
            TimeUnit.MILLISECONDS.sleep(4000);

            String buyOrSell = args[1];
            boolean buying = false;
            boolean selling = false;

            if (buyOrSell.equals("buy")) {
                String buy = "document.querySelectorAll('button[class=\"action-button--trade js-action-button--trade\"]')[0].click();";
                js.executeScript(buy);
                buying = true;
                selling = false;
            } else if (buyOrSell.equals("sell")) {
                String sell = "document.querySelectorAll('button[class=\"action-button--trade js-action-button--trade\"]')[1].click();";
                js.executeScript(sell);
                buying = false;
                selling = true;
            }
                    
            TimeUnit.MILLISECONDS.sleep(1000);


            String desiredCostStr = getCodeFromKeyboardInput("Set for Limit Order, press <ENTER> to continue:");
            float desiredCost = Float.parseFloat(desiredCostStr);
            
            //no reason to do anything until i set up limit order etc
            String setCostSelector = "input[id=st-form-container--limit-price]";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(setCostSelector)));

            String refresh = "document.querySelector('a[class=\"refresh-icon refresh-balances\"]').click()";
            String getCostSelector = ".price strong";
            for (int i=0;i<10;i++) {
                js.executeScript(refresh);
                TimeUnit.MILLISECONDS.sleep(600);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(getCostSelector)));
                WebElement getCostElem = driver.findElement(By.cssSelector(getCostSelector));
                String realCostStr = getCostElem.getText();
                float realCost = Float.parseFloat(realCostStr);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(setCostSelector)));
                WebElement costInputElem = driver.findElement(By.cssSelector(setCostSelector));
                costInputElem.sendKeys(Keys.CONTROL,"a");
                TimeUnit.MILLISECONDS.sleep(200);
                costInputElem.sendKeys(realCostStr);
                //TimeUnit.MILLISECONDS.sleep(200);
                if (buying && realCost <= desiredCost) {
                	break;
                } else if (selling && realCost >= desiredCost) {
                	break;
                }
            }

            driver.close();

        } catch (Exception e) {
            driver.close();
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
            new Fidelity().doIt(args);
    }
}
