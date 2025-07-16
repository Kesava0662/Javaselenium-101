package workflows;
import common.*;
import java.util.stream.Collectors;
import java.util.*;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.text.DateFormat;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.http.Headers;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import org.openqa.selenium.interactions.Actions;
import java.nio.file.Path;
import org.openqa.selenium.Keys;
import java.nio.file.Paths;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogEntries;
import java.io.IOException;
import java.awt.Color;
import io.percy.selenium.Percy;
import java.io.ByteArrayInputStream;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
    
@SuppressWarnings("all")
public class SeleniumWorkFlow
{
  public static WebDriver browser;
	private static Percy percy;
	private static String percyFlag = CommonUtil.getXMLData(Constants.APPLICATION_SETTING_PATH, "Percy");
	private static String language = CommonUtil.getXMLData(Constants.APPLICATION_SETTING_PATH, "Language");
	private static String yes = "Yes";
	static final Logger log = Logger.getLogger(SeleniumWorkFlow.class);

 		 
  public void accessToPage()
  {
    WebBrowser.LaunchApplication(true);
    log.info("Method accessToPage completed.");
  }
  public void scrollAndClick(int index,String page,String xpathKey, String identifier)
  {
    try
    {
      WebBrowser.setCurrentBrowser(index);
      browser=WebBrowser.getBrowser();
      WebBrowserUtil.attachPage(page);
      String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
      WebBrowserUtil.scrollAndClickUsingJS(xpath,identifier);
    }
    catch(Exception e)
    {
      throw new CustomException(e.getMessage(), e);
    }
  }public void enterText(String textToBeEntered,int index, String page,String xpathKey,String identifier)
    {
        WebBrowser.setCurrentBrowser(index);
        browser=WebBrowser.getBrowser();
        WebBrowserUtil.attachPage(page);
        textToBeEntered= CommonUtil.getData(textToBeEntered);
      try
          {
            String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
            WebElement element = WebBrowserUtil.findElement(xpath,identifier);
            if (element != null) {
                String elementHtml = element.getAttribute("outerHTML");
                CommonUtil.storeElementInJson(xpath, elementHtml);
            } else {
                throw new CustomException("Element not found for XPath: " + xpath);
            }
            WebBrowserUtil.enterText(element, textToBeEntered);
            log.info("Method enterText completed.");
          }
      catch(Exception e)
          {
            log.error("Method enterText not completed."+e);
            throw new CustomException(e.getMessage(), e);
      }
  }
  public void clearText(int index,String page,String xpathKey, String identifier)
  {
    WebBrowser.setCurrentBrowser(index);
    browser=WebBrowser.getBrowser();
    WebBrowserUtil.attachPage(page);
    String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
    WebBrowserUtil.clearText(WebBrowserUtil.findElement(xpath,identifier));
  }
  public void enterCopiedText(int index,String page,String xpathKey, String identifier)
  {
    WebBrowser.setCurrentBrowser(index);
    browser=WebBrowser.getBrowser();
    WebBrowserUtil.attachPage(page);
    boolean staleElement = true;
    int i = 0;
    String param="";
    String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
    while(staleElement && i < Constants.NUMBER_OF_ITERATION)
    {
      try
      {
        param=String.valueOf(CommonUtil.getCopiedText());
        WebBrowserUtil.findElement(xpath,identifier).sendKeys(param);
        staleElement = false;
      }
      catch(Exception ex)
      {
        i++;
        staleElement = true;
        try
        {
          WebBrowserUtil.scrollAndEnterText(WebBrowserUtil.findElement(xpath,identifier),param);
          staleElement = false;
        }
        catch (Exception exc)
        {}
        if(i == 4)
        {
          throw new CustomException(ex.getMessage(),ex);
        }
      }
    }
  }
  public void clearAndEnterText(String param,int index,String page,String xpathKey, String identifier)
  {
    WebBrowser.setCurrentBrowser(index);
    browser=WebBrowser.getBrowser();
    WebBrowserUtil.attachPage(page);
    param = CommonUtil.getData(param);
    param  = param.replace("_space_"," ");
    String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
    WebBrowserUtil.clearAndEnterText(WebBrowserUtil.findElement(xpath,identifier),param);
  }
  public void copiedtext(int index,String page,String xpathKey, String identifier)
  {
    WebBrowser.setCurrentBrowser(index);
    browser=WebBrowser.getBrowser();
    WebBrowserUtil.attachPage(page);
    String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);
    String copiedValue=WebBrowserUtil.getText(WebBrowserUtil.findElement(xpath, identifier));
    if (copiedValue.contains("C:\\fakepath\\"))
    {
      Path p = Paths.get(copiedValue);
      copiedValue = p.getFileName().toString();
    }
    CommonUtil.setCopiedText(copiedValue);
    log.info("Method copiedtext completed.");
    System.out.println(copiedValue);
    ExtentCucumberAdapter.addTestStepLog("Copied value: "+copiedValue);
  }
    public void clickedElement(int index,String page,String xpathKey,String identifier)
    {
      WebBrowser.setCurrentBrowser(index);
      browser=WebBrowser.getBrowser();
      WebBrowserUtil.attachPage(page);
      WebElement elementToBeClicked = null;
      try
      {
        String xpath=YMLUtil.getYMLObjectRepositoryData(xpathKey);        
    		boolean staleElement = true;
    		int i = 0;
        elementToBeClicked = WebBrowserUtil.findElement(xpath,identifier);
        if (elementToBeClicked != null) {
            String elementHtml = elementToBeClicked.getAttribute("outerHTML");
            CommonUtil.storeElementInJson(xpath, elementHtml);
        } else {
            throw new CustomException("Element not found for XPath: " + xpath);
        }
    		while (staleElement && i < 5) {
    			try {
    				WebBrowserUtil.click(elementToBeClicked);
    				staleElement = false;
    			} catch (Exception ex) {
    				i++;
    				staleElement = true;
    				try {
    					WebBrowserUtil.scrollAndClickUsingJS(elementToBeClicked);
    					staleElement = false;
    				} catch (Exception exc) {
              log.error(exc.getMessage());
    				}
    				if (i == 4) {
    					throw new CustomException(ex.getMessage(), ex);
    				}
    			}
    		}        
            log.info("Method clickedElement completed.");
      }
      catch(Exception e)
      {
        log.error("Method clickedElement not completed."+e);
        throw new CustomException(e.getMessage(), e);
      }
  }
  public boolean verifyDefaultpageIsdisplayed(String defaultpage)
  {
    boolean isVerified = false;
    for (int i = 0; i <= 2; i++)
    {
      WebBrowser.setCurrentBrowser(0);
      browser=WebBrowser.getBrowser();
      try{
        browser = WebBrowserUtil.attachPage(defaultpage);
      }
      catch(Exception ex){
      log.error(ex.getMessage());
      }
      if (defaultpage != null && !defaultpage.isEmpty()  && !defaultpage.toUpperCase().equals("NA".toUpperCase()) && !defaultpage.toUpperCase().equals("Page1".toUpperCase()) && !defaultpage.toUpperCase().equals("Page2".toUpperCase()) && !defaultpage.toUpperCase().equals("Page3".toUpperCase())&& !defaultpage.toUpperCase().equals("Page4".toUpperCase()))
      {
        isVerified = browser.getTitle().toUpperCase().contains(defaultpage.toUpperCase());
      }
      else
      {
        isVerified = true;
      }
      if (isVerified)
      {
        break;
      }
    }
      try
      {
        ExtentCucumberAdapter.addTestStepLog("Actual Page Name: "+browser.getTitle()+", Expected Page Name: "+defaultpage);
        log.info("Method VerifyDefaultpageIsdisplayed completed.");
      }
      catch(Exception e)
      {
        log.error("Method VerifyDefaultpageIsdisplayed not completed."+e);
      }
    return isVerified;
  }
  public boolean verifymessageIsDisplayed(String message)
  {
   boolean isVerified = false;
		String[] messages;
		String[] messages1;
		List<String> boolvalues = new ArrayList<>();

		if (message.contains("verifyalert_")) {
			String[] msgs = message.split("_");
			return WebBrowserUtil.verifyAlertText(msgs[1]);
		}

		if ((message.contains("frameid_")) && (message.contains("&or&"))) {
			String[] msgs = message.split("_");
			StringBuilder frameID = new StringBuilder();

			for (int i = 0; i < msgs.length - 1; i++) {
				if (i != 0) {
					frameID.append(msgs[i]).append("_");
				}
			}
			String frameIDStr = frameID.toString().replaceAll("_$", "");
			WebBrowserUtil.getFrame(frameIDStr);

			String[] multiplemessages = msgs[msgs.length - 1].split("&or&");
			for (String multiplemessage : multiplemessages) {
				if (WebBrowserUtil.isElementPresent(multiplemessage)) {
					isVerified = true;
				}
			}
			return isVerified;
		}

		if (message.contains("frameid_")) {
			String[] msgs = message.split("_");
			StringBuilder frameID = new StringBuilder();

			for (int i = 0; i < msgs.length - 1; i++) {
				if (i != 0) {
					frameID.append(msgs[i]).append("_");
				}
			}
			String frameIDStr = frameID.toString().replaceAll("_$", "");
			WebBrowserUtil.getFrame(frameIDStr);

			isVerified = WebBrowserUtil.isElementPresent(msgs[msgs.length - 1]);
			return isVerified;
		}

		if (message.contains(")_or_(")) {
			messages = message.split(")_or_(");
			for (String msg : messages) {
				if (msg.equalsIgnoreCase("NA")) {
					return true;
				}
				messages1 = msg.split("_and_");
				boolvalues.clear();

				for (String msg1 : messages1) {
					String messagetocheck = msg1.replace("(", "").replace(")", "");
					isVerified = WebBrowserUtil.isElementPresent(messagetocheck);
					if (isVerified) {
						boolvalues.add(Boolean.toString(isVerified));
					}
				}

				if (messages1.length == boolvalues.size()) {
					Assert.assertTrue(isVerified);
					return isVerified;
				}
			}
			return isVerified;
		}

		if (message.contains("_and_")) {
			messages = message.split("_and_");
			for (String msg : messages) {
				isVerified = WebBrowserUtil.isElementPresent(msg);
				if (!isVerified) {
					return false;
				}
			}
			return true;
		}

		if (message.contains("_or_")) {
			messages = message.split("_or_");
			for (String msg : messages) {
				if (msg.equals("NA")) {
					Assert.assertTrue(isVerified);
					return true;
				}
				isVerified = WebBrowserUtil.isElementPresent(msg);
				if (isVerified) {
					Assert.assertTrue(isVerified);
					return true;
				}
			}
			return false;
		}

		if (!message.equals("NA")) {
			for (int i = 0; i <= 2; i++) {
				isVerified = WebBrowserUtil.isElementPresent(message);
				String content = WebBrowserUtil.getContent(message);
				if (isVerified) {
					Assert.assertTrue(isVerified);
					if (content != null && !content.isEmpty()) {
						ExtentCucumberAdapter
								.addTestStepLog("Actual content: " + content + ", Expected content: " + message);
						log.info("Method verifymessageIsDisplayed completed.");
					}
					return true;
				}
			}
			return false;
		}
		return true;
  }
		 
    }