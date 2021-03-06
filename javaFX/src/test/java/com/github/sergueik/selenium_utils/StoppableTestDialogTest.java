package com.github.sergueik.selenium_utils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import java.lang.IllegalStateException;
// to ignore the javaFx exception on atttempt to launch Application more than once 

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
 * Stoppable test example - JavaFx
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class StoppableTestDialogTest {
	public int scriptTimeout = 5;
	public int flexibleWait = 60; // possibly too long
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
	// public Alert alert;
	public JavascriptExecutor js;
	public TakesScreenshot screenshot;

	@SuppressWarnings("deprecation")
	@BeforeClass
	public void beforeClass() {

		System.setProperty("webdriver.chrome.driver",
				osName.equals("windows")
						? (new File("c:/java/selenium/chromedriver.exe")).getAbsolutePath()
						: Paths.get(System.getProperty("user.home")).resolve("Downloads")
								.resolve("chromedriver").toAbsolutePath().toString());

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		ChromeOptions chromeOptions = new ChromeOptions();

		Map<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		String downloadFilepath = System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "target"
				+ System.getProperty("file.separator");
		chromePrefs.put("download.prompt_for_download", "false");
		chromePrefs.put("download.directory_upgrade", "true");
		chromePrefs.put("plugins.always_open_pdf_externally", "true");
		chromePrefs.put("download.default_directory", downloadFilepath);
		chromePrefs.put("enableNetwork", "true");
		// https://stackoverflow.com/questions/18106588/how-to-disable-cookies-using-webdriver-for-chrome-and-firefox-java
		// chromePrefs.put("profile.default_content_settings.cookies", 2);
		// no cookies are allowed

		chromeOptions.setExperimentalOption("prefs", chromePrefs);
		if (osName.equals("windows")) {
			// TODO: use jni to find out the CPU arch
			if (System.getProperty("os.arch").contains("64")) {
				String[] paths = new String[] {
						"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
						"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" };
				// check file existence
				for (String path : paths) {
					File exe = new File(path);
					System.err.println("Inspecting browser path: " + path);
					if (exe.exists()) {
						chromeOptions.setBinary(path);
					}
				}
			} else {
				chromeOptions.setBinary(
						"c:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
			}
		} else {
		}
		for (String optionAgrument : (new String[] {
				"--user-agent=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20120101 Firefox/33.0",
				"--allow-running-insecure-content", "--allow-insecure-localhost",
				"--enable-local-file-accesses", "--disable-notifications",
				"--disable-save-password-bubble",
				/* "start-maximized" , */
				"--disable-default-app", "disable-infobars", "--no-sandbox ",
				"--browser.download.folderList=2", "--disable-web-security",
				"--disable-translate", "--disable-popup-blocking",
				"--ignore-certificate-errors", "--no-proxy-server",
				"--browser.helperApps.neverAsk.saveToDisk=image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf",
				String.format("--browser.download.dir=%s", downloadFilepath)
				/* "--user-data-dir=/path/to/your/custom/profile"  , */

		})) {
			chromeOptions.addArguments(optionAgrument);
		}

		// options for headless
		if (headless) {
			for (String optionAgrument : (new String[] { "headless",
					"window-size=1200x800" })) {
				chromeOptions.addArguments(optionAgrument);
			}
		}

		capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
		capabilities.setCapability(
				org.openqa.selenium.chrome.ChromeOptions.CAPABILITY, chromeOptions);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		// https://stackoverflow.com/questions/48851036/how-to-configure-log-level-for-selenium
		// https://stackoverflow.com/questions/28572783/no-log4j2-configuration-file-found-using-default-configuration-logging-only-er
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
		logPrefs.enable(LogType.BROWSER, Level.INFO);
		logPrefs.enable(LogType.DRIVER, Level.INFO);
		/*
			logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
			logPrefs.enable(LogType.BROWSER, Level.ALL);
			logPrefs.enable(LogType.DRIVER, Level.ALL);
		*/
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

		driver = new ChromeDriver(capabilities);

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
		if (driver != null) {
			driver.quit();
		}
	}

	@Test(enabled = false)
	public void testDialogTest1() {
		// String handle = createWindow(altURL);
		String name = "Window_" + instanceCount++;
		// inject an anchor element - will likely appear at the bottom of the page
		injectElement(name);
		WebElement element = driver.findElement(By.id(name));
		sleep(1000);
		// scroll to the new page element
		scroll(element);
		// stop the test until user chooses to continue
		System.err.println("Hold the test1: Creating new dialog on the display");
		try {
			TestDialog.main(new String[] { "5" });
		} catch (IllegalStateException e) {
			// Application launch must not be called more than once
			// https://stackoverflow.com/questions/24320014/how-to-call-launch-more-than-once-in-java
			// for an JavaFX application that is not allowed.
			// https://stackoverflow.com/questions/38707913/java-lang-illegalstateexception-application-launch-must-not-be-called-more-than?noredirect=1&lq=1
			// see also:
			// https://stackoverflow.com/questions/46328192/javafx-illegalargumentexception-is-already-set-as-root-of-another-scene
			System.err.println("Ignore the exception from javaFx " + e.toString());
		}
		// continue the test
		System.err.println("Continue the test");
		element.click();
		// TODO: deal with handles and waits to produce a consistent behavior
		sleep(5000);
	}

	@Test(enabled = false)
	public void testDialogTest2() {
		driver.navigate().to("http://www.wikipedia.org/");
		// String handle = createWindow(altURL);
		// stop the test until user chooses to continue
		System.err.println("Hold the test2: Creating new dialog on the display");
		TestDialog.main2(new String[] { "10" });
		// continue the test
		System.err.println("Continue the test");
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
