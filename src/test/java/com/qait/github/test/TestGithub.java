package com.qait.github.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
import org.testng.*;
import org.testng.annotations.*;

import com.qait.generalActions.GeneralActions;
import com.qait.generalActions.Util;
import com.qait.github.pagefactory.*;
import com.qait.unix.*;
import com.qait.utility.*;

public class TestGithub {
	WebDriver webdriver;
	GeneralActions actions;
	GithubSpashPage githubSpashPage;
	GithubSignInPage githubSignInPage;
	GithubHomePage githubHomePage;
	GithubCreateNewRepositoryPage githubCreateNewRepositoryPage;
	GithubNewRepositoryPage githubNewRepositoryPage;
	GithubRepositoryPage githubRepositoryPage;
	GithubLatestCommitPage githubLatestCommitPage;
	
	String gitUrl;
	
	@BeforeClass
	public void setup(){
		actions= new GeneralActions();
		webdriver= actions.getDriver(Util.getConfigValue("browser"));
		actions.setDriver(webdriver);
		webdriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		actions.getURL(Util.getConfigValue("url"));
	}
	
	@Test(priority=1, enabled=true)
	public void testCreateRepository(){
		githubSpashPage = new GithubSpashPage(webdriver);
		Assert.assertEquals(webdriver.getTitle(), "How people build software · GitHub");
		githubSignInPage = githubSpashPage.clickSignIn();
		Assert.assertEquals(githubSignInPage.getTitle(webdriver), "Sign in to GitHub · GitHub");
		githubHomePage = githubSignInPage.login(Utility.getYamlValues("username"),Utility.getYamlValues("password"));
		Assert.assertEquals(githubHomePage.getTitle(webdriver), "GitHub");
		githubCreateNewRepositoryPage = githubHomePage.clickNewRepository();
		Assert.assertEquals(githubCreateNewRepositoryPage.getTitle(webdriver), "Create a New Repository");
		githubNewRepositoryPage = githubCreateNewRepositoryPage.createValidRepository();
		Assert.assertEquals(githubNewRepositoryPage.getTitle(webdriver), Utility.getYamlValues("username")+"/"+Utility.getYamlValues("repositoryname"));
		Assert.assertEquals(githubNewRepositoryPage.isGithubRemoteLinkDisplayed(), true);
	}
	

	@Test(priority=2)
	public void testCommandExec(){
		System.out.println((new java.util.Date()).getTime());
		List<String> commands = new ArrayList<String>();
		commands.add("mkdir "+Utility.getYamlValues("repositoryname"));
		commands.add("cd "+Utility.getYamlValues("repositoryname"));
	    commands.add("touch "+Utility.getYamlValues("filename"));
	    commands.add("git init");		
	    commands.add("git remote add origin 'https://"+Utility.getYamlValues("username")+":"+Utility.getYamlValues("password")+"@github.com/"+Utility.getYamlValues("username")+"/"+Utility.getYamlValues("repositoryname")+".git'");
	    commands.add("git add .");
	    commands.add("git commit -m \""+(new java.util.Date()).getTime()+"\"");
	    commands.add("git push "+"'https://"+Utility.getYamlValues("username")+":"+Utility.getYamlValues("password")+"@github.com/"+Utility.getYamlValues("username")+"/"+Utility.getYamlValues("repositoryname")+".git'"+" master");
		Utility.writeToFile("src/main/resources/gitCommands.sh",commands);
		CommandExecutor gitCommands = new CommandExecutor();
		String[] str_arr = {"sh","src/main/resources/gitCommands.sh"};
		System.out.println(gitCommands.execCommand(str_arr));
	}
	
	@Test(dependsOnMethods = "testCommandExec")
	public void validatePushedFile() throws InterruptedException{
		githubRepositoryPage = githubNewRepositoryPage.newRepositoryPageRefresh();
		Assert.assertEquals(githubRepositoryPage.getTitle(webdriver), Utility.getYamlValues("username")+"/"+Utility.getYamlValues("repositoryname"));
		Assert.assertEquals(Utility.getYamlValues("filename"), githubRepositoryPage.getFileName());
	}
	
	@Test(dependsOnMethods = "validatePushedFile")
	public void validateLatestCommit(){
		CommandExecutor gitCommands = new CommandExecutor();
		String[] str_arr = {"git","log"};
		String output = gitCommands.execCommand(str_arr, Utility.getYamlValues("repositoryname")+"/");
		String line[] = output.split("\n");
		String word[]=line[0].split(" ");
		System.out.println(word[1]);
		githubLatestCommitPage = githubRepositoryPage.clickLatestCommitLink();
		Assert.assertEquals(githubLatestCommitPage.getLatestCommit(), word[1]);
	}
}