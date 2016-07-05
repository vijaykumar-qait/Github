package com.qait.github.pagefactory;
import org.openqa.selenium.WebDriver;
public class BaseClass {
	
	public String getTitle(WebDriver driver) {
		return driver.getTitle();
	}
}

