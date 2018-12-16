package com.github.sergueik.selenium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import java.lang.reflect.Method;

import java.time.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.openqa.selenium.Alert;
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
 * Stoppable test example
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class StoppableTest {
	public int scriptTimeout = 5;
	public int flexibleWait = 60; // too long
	public int implicitWait = 1;
	public int pollingInterval = 500;

	private static final boolean headless = Boolean
			.parseBoolean(System.getenv("HEADLESS"));
	private static String baseURL = "https://www.urbandictionary.com/";
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
		driver.quit();
	}

	@Test(enabled = true)
	public void test1() {
		// String handle = createWindow(altURL);

		// Inject an anchor element
		String name = "Window_" + instanceCount++;
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

		WebElement element = driver.findElement(By.id(name));
		sleep(1000);
		try {
			// element.getLocation()
			Point location = element.getLocation();
			System.err.println("Scrolling to " + location.y);
			scroll(location.x, location.y);
		} catch (UnsupportedCommandException e) {
			System.err.println("Exception (ignored) " + e.toString());
			// ignore
		}

		final Display display = new Display();
		final Shell shell = new Shell(display);

		System.err.println("Hold the test");
		System.err.println("Creating new dialog on the display");
		(new BlockTestDialogEx(shell)).open();
		System.err.println("Finish the test");

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

	private static class BlockTestDialogEx extends IconAndMessageDialog {

		public static final int CONTINUE_ID = IDialogConstants.CLIENT_ID;
		public static final String CONTINUE_LABEL = "Continue";
		private Image image;
		@SuppressWarnings("unused")
		private Label label;
		@SuppressWarnings("unused")
		private String message;

		public BlockTestDialogEx(Shell parent) {
			super(parent);

			// Create the image
			try {
				image = new Image(parent.getDisplay(), new FileInputStream(
						"src\\main\\resources\\images\\watchglass.png"));
			} catch (FileNotFoundException e) {
				System.err.println("Exception: " + e.toString());
			}

			// Set the default message
			message = "Continue test?";
		}

		@SuppressWarnings("unused")
		public void setMessage(String message) {
			this.message = message;
		}

		public boolean close() {
			if (image != null)
				image.dispose();
			return super.close();
		}

		protected Control createDialogArea(Composite parent) {
			createMessageArea(parent);
			// Create a composite to hold the label
			Composite composite = new Composite(parent, SWT.NONE);
			// Create the label for the "hidden" message
			// label = new Label(composite, SWT.LEFT);
			return composite;
		}

		protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, CONTINUE_ID, CONTINUE_LABEL, false);
		}

		protected void buttonPressed(int buttonId) {
			if (buttonId == CONTINUE_ID) {
				setReturnCode(buttonId);
				close();
			}
		}

		protected Image getImage() {
			return image;
		}
	}
}
