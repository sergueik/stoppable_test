package com.github.sergueik.selenium_utils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Stoppable test example (Swing version)
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class StoppableTest {
	public int scriptTimeout = 5;
	public int flexibleWait = 60; // too long
	public int implicitWait = 1;
	public int pollingInterval = 500;

	private static final boolean headless = Boolean
			.parseBoolean(System.getenv("HEADLESS"));
	private static String baseURL = "https://www.linux.org"; // "https://www.urbandictionary.com/";
	// NOTE: some sites may be blocked via content filtering
	// Sorry, www.urbandictionary.com has been blocked by your network
	// administrator.
	private static String osName = getOSName();
	private static int instanceCount = 0;
	private static String altURL = "https://www.linux.org.ru/";
	private static final StringBuffer verificationErrors = new StringBuffer();
	public WebDriver driver;
	public WebDriverWait wait;
	public Actions actions;
	public Alert alert;
	public JavascriptExecutor js;
	public TakesScreenshot screenshot;

	@SuppressWarnings("deprecation")
	@BeforeClass
	public void beforeClass() {

		// earlier versions firefox and grckodriver needed
		// "webdriver.firefox.marionette"
		System.setProperty("webdriver.gecko.driver",
				osName.equals("windows")
						? (new File("c:/java/selenium/geckodriver.exe")).getAbsolutePath()
						: Paths.get(System.getProperty("user.home")).resolve("Downloads")
								.resolve("geckodriver").toAbsolutePath().toString());

		System.setProperty("webdriver.firefox.bin",
				osName.equals("windows")
						? new File("c:/Program Files (x86)/Mozilla Firefox/firefox.exe")
								.getAbsolutePath()
						: "/usr/bin/firefox");

		// https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		// use legacy FirefoxDriver
		// for Firefox v.59 no longer possible ?
		capabilities.setCapability("marionette", false);
		// http://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.firefox.FirefoxProfile
		capabilities.setCapability("locationContextEnabled", false);
		capabilities.setCapability("acceptSslCerts", true);
		capabilities.setCapability("elementScrollBehavior", 1);
		FirefoxProfile profile = new FirefoxProfile();
		// NOTE: the setting below may be too restrictive
		// http://kb.mozillazine.org/Network.cookie.cookieBehavior
		// profile.setPreference("network.cookie.cookieBehavior", 2);
		// no cookies are allowed
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/octet-stream,text/csv");
		profile.setPreference("browser.helperApps.neverAsk.openFile",
				"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
		// TODO: cannot find symbol: method
		// addPreference(java.lang.String,java.lang.String)location: variable
		// profile of type org.openqa.selenium.firefox.FirefoxProfile
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
		// http://learn-automation.com/handle-untrusted-certificate-selenium/
		profile.setAcceptUntrustedCertificates(true);
		profile.setAssumeUntrustedCertificateIssuer(true);

		// NOTE: ERROR StatusLogger No log4j2 configuration file found. Using
		// default configuration: logging only errors to the console.
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
		logPrefs.enable(LogType.PROFILER, Level.INFO);
		logPrefs.enable(LogType.BROWSER, Level.INFO);
		logPrefs.enable(LogType.CLIENT, Level.INFO);
		logPrefs.enable(LogType.DRIVER, Level.INFO);
		logPrefs.enable(LogType.SERVER, Level.INFO);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

		profile.setPreference("webdriver.firefox.logfile", "/dev/null");
		// NOTE: the next setting appears to have no effect.
		// does one really need os-specific definition?
		// like /dev/null for Linux vs. nul for Windows
		System.setProperty("webdriver.firefox.logfile",
				osName.equals("windows") ? "nul" : "/dev/null");

		// no longer supported as of Selenium 3.8.x
		// profile.setEnableNativeEvents(false);
		profile.setPreference("dom.webnotifications.enabled", false);
		// optional
		/*
		 * profile.setPreference("general.useragent.override",
		 * "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20120101 Firefox/33.0");
		 */
		// System.err.println(System.getProperty("user.dir"));
		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		try {
			driver = new FirefoxDriver(capabilities);
			// driver.setLogLevel(FirefoxDriverLogLevel.ERROR);
		} catch (WebDriverException e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Cannot initialize Firefox driver: " + e.toString());
			// java.lang.RuntimeException:
			// Cannot initialize Firefox driver:
			// org.openqa.selenium.firefox.UnableToCreateProfileException:
			// java.io.IOException: Can only install from a zip file, an XPI or a
			// directory: /home/sergueik/Downloads/geckodriver
		}

		driver.manage().timeouts().implicitlyWait(180, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
		// driver.setLogLevel(Level.ALL);
		wait = new WebDriverWait(driver, flexibleWait);

		// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older
		wait.pollingEvery(Duration.ofMillis(pollingInterval));
		// wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);

		screenshot = ((TakesScreenshot) driver);
		js = ((JavascriptExecutor) driver);

	}

	@BeforeMethod
	public void BeforeMethod(Method method) {

		driver.get(baseURL);
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", baseURL));
		wait.until(urlChange);
		System.err.println("BeforeMethod: Current  URL: " + driver.getCurrentUrl());
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		driver.get("about:blank");
	}

	@AfterTest(alwaysRun = true)
	public void afterTest() {
		System.err.println("Finish the test");
		driver.quit();
	}

	@Test(enabled = true)
	public void exampleTest() {
		// String handle = createWindow(altURL);
		String name = "Window_" + instanceCount++;
		// inject an anchor element - will likely appear at the bottom of the page
		injectElement(name);
		WebElement element = driver.findElement(By.id(name));
		sleep(1000);
		// scroll to the new page element
		scroll(element);
		// stop the test until user chooses to continue
		System.err
				.println("Hold the test: creating new Swing dialog on the display");
		// new TestDialog();
		new TestDialog("exampleTest", true, 2);
		// continue the test
		System.err.println("Continue the test");
		element.click();
		// TODO: deal with handles and waits to produce a consistent behavior
		sleep(5000);
	}

	private void injectElement(String name) {

		// Inject an anchor element
		executeScript(
				"var anchorTag = document.createElement('a'); "
						+ "anchorTag.appendChild(document.createTextNode('nwh'));"
						+ "anchorTag.setAttribute('id', arguments[0]);"
						+ "anchorTag.setAttribute('href', arguments[1]);"
						+ "anchorTag.setAttribute('target', '_blank');"
						+ "anchorTag.setAttribute('style', 'display:block;');"
						+ "var firstElement = document.getElementsByTagName('body')[0].getElementsByTagName('*')[0];"
						+ "firstElement.parentElement.appendChild(anchorTag);",
				name, altURL);
		// common error with this approach: Element is not clickable at point
		// HTML, HEAD, BODY, some element
	}

	private void scroll(WebElement element) {
		try {
			// element.getLocation()
			Point location = element.getLocation();
			System.err.println("Scrolling to " + location.y);
			scroll(location.x, location.y);
		} catch (UnsupportedCommandException e) {
			System.err.println("Exception (ignored) " + e.toString());
			// ignore
		}
	}

	// Scroll
	public void scroll(final int x, final int y) {
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		for (int i = 0; i <= x; i = i + 50) {
			js.executeScript("scroll(" + i + ",0)");
		}
		for (int j = 0; j <= y; j = j + 50) {
			js.executeScript("scroll(0," + j + ")");
		}
	}

	// Utilities
	private static String getOSName() {
		if (osName == null) {
			osName = System.getProperty("os.name").toLowerCase();
			if (osName.startsWith("windows")) {
				osName = "windows";
			}
		}
		return osName;
	}

	public void sleep(Integer milliSeconds) {
		try {
			Thread.sleep((long) milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}
}
