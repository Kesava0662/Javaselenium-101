package stepdefinitions;
    
import io.cucumber.java.en.*;
import workflows.SeleniumWorkFlow;
import common.*;

  @SuppressWarnings("all")
  public class NewFeatureStepDefinition
	 {
      SeleniumWorkFlow workFlow = new SeleniumWorkFlow();
      
            @When("^I scroll and click Form Auth link in the internet$")			
            public void whenIScrollClickFormAuthLinkInTheInternet()
            {
                workFlow.scrollAndClick(0, "New Feature", "New Feature.FormAuthlinkLinkXPATH", "XPATH");
                
            }
            @When("^I entered Username_TB in form authentication as '(.*)'$")			
            public void whenIEnteredUsernametbInFormAuthenticationAsusernametb1(String  varusernametb1)
            {
                workFlow.enterText(varusernametb1, 0, "New Feature", "New Feature.Username_TBTextBoxXPATH", "XPATH");
                
            }
            @When("^I clear text Username_TB in form authentication$")			
            public void whenIClearTextUsernametbInFormAuthentication()
            {
                workFlow.clearText(0, "New Feature", "New Feature.Username_TBTextBoxXPATH", "XPATH");
                
            }
            @When("^I enter copied text Username_TB in form authentication$")			
            public void whenIEnterCopiedTextUsernametbInFormAuthentication()
            {
                workFlow.enterCopiedText(0, "New Feature", "New Feature.Username_TBTextBoxXPATH", "XPATH");
                
            }
            @When("^I clear and enter text Username_TB in form authentication as '(.*)'$")			
            public void whenIClearEnterTextUsernametbInFormAuthenticationAsusernametb2(String  varusernametb2)
            {
                workFlow.clearAndEnterText(varusernametb2, 0, "New Feature", "New Feature.Username_TBTextBoxXPATH", "XPATH");
                
            }
    }