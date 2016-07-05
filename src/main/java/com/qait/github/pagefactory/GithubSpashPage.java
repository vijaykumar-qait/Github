package com.qait.github.pagefactory;


import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
public class GithubSpashPage extends BaseClass{
	private WebDriver driver;
	
	@FindBy(linkText="Sign in")
	private WebElement signInLink;
	
	public GithubSpashPage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public GithubSignInPage clickSignIn(){
		signInLink.click();
		return new GithubSignInPage(driver);
	}
}