package com.github.sergueik.selenium_utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import java.lang.reflect.Method;

import java.time.Duration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Formatter;
import java.util.logging.Level;

import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
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
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;

/**
 * Stoppable test example (eclipse SWT version)
 * for login page with an arithmetic captcha
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ServiziCookieTest {
	public int scriptTimeout = 5;
	public int flexibleWait = 60; // too long
	public int implicitWait = 1;
	public int pollingInterval = 500;
	private static long highlightInterval = 100;
	private static final String usernome = getPropertyEnv("TEST_USER",
			"testuser");
	private static final String passe = getPropertyEnv("TEST_PASS", "00000000");
	private static Formatter formatter;
	private static StringBuilder loggingSb;

	private static final boolean headless = Boolean
			.parseBoolean(System.getenv("HEADLESS"));

	private static final String baseURL = "http://bandi.servizi.politicheagricole.it/taxcredit/default.aspx";
	private static final String landURL = "http://bandi.servizi.politicheagricole.it/taxcredit/menu.aspx";
	private static String osName = getOSName();
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

		loggingSb = new StringBuilder();
		formatter = new Formatter(loggingSb, Locale.US);
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
		// java has no precompiler #ifdef
		// wait.pollingEvery(Duration.ofMillis(pollingInterval));
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);

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

	@Test(enabled = false)
	public void getCookieTest() {
		// String handle = createWindow(altURL);
		driver.get(baseURL);
		WebElement element = driver
				.findElement(By.xpath("//*[contains(text(), 'A C C E D I')]"));
		highlight(element);
		element.click();
		wait.until(ExpectedConditions.urlContains(baseURL));

		element = driver.findElement(By.id("ctl00_phContent_Login_txtEmail"));
		highlight(element);
		element.clear();
		element.sendKeys(usernome);
		element = driver.findElement(By.id("ctl00_phContent_Login_txtOTP"));
		highlight(element);
		element.clear();
		element.sendKeys(passe);

		sleep(1000);
		final Display display = new Display();
		final Shell shell = new Shell(display);

		System.err.println("Hold the test");
		System.err.println("Creating new dialog on the display");
		BlockTestDialogEx blockTestDialog = new BlockTestDialogEx(shell);
		blockTestDialog.setMessage("This is Selenium test supplied message...");
		blockTestDialog.setButtonText("Continue test");
		blockTestDialog.setContinueText("Continue the test");
		blockTestDialog.open();

		element = driver.findElement(
				By.xpath("//input[contains(@name,'Login')][@value='ACCEDI']"));
		highlight(element);

		// continue the test
		element.click();
		sleep(5000);

		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Cookies:");
		JSONArray cookieJSONArray = new JSONArray();
		for (Cookie cookie : cookies) {
			System.err.println(
					formatter.format("Name: '%s'\n", cookie.getName()).toString());
			System.err.println(
					formatter.format("Value: '%s'\n", cookie.getValue()).toString());
			System.err.println(
					formatter.format("Domain: '%s'\n", cookie.getDomain()).toString());
			System.err.println(
					formatter.format("Path: '%s'\n", cookie.getPath()).toString());
			System.err.println(
					formatter.format("Expiry: '%tc'\n", cookie.getExpiry()).toString());
			System.err.println(formatter.format("Secure: '%b'\n", cookie.isSecure()));
			System.err
					.println(formatter.format("HttpOnly: '%b'\n", cookie.isHttpOnly()));
			/*
						System.err.println(formatter
			.format(
			"Name: '%s'\n" + "Value: '%s'\n" + "Domain: '%s'\n"
			+ "Path: '%s'\n" + "Expiry: '%tc'\n" + "Secure: '%b'\n"
			+ "HttpOnly: '%b'\n" + "\n",
			cookie.getName(), cookie.getValue(), cookie.getDomain(),
			cookie.getPath(), cookie.getExpiry(), cookie.isSecure(),
			cookie.isHttpOnly())
			.toString());
			*/
			JSONObject cookieJSONObject = new JSONObject(cookie);
			System.err.println(cookieJSONObject.toString());
			cookieJSONArray.put(cookieJSONObject);
		}
		JSONObject cookiesJSONObject = new JSONObject();
		try {
			cookiesJSONObject.put("cookies", cookieJSONArray);
		} catch (JSONException e) {

		}
		System.err.println(cookiesJSONObject.toString());

	}

	@SuppressWarnings("deprecation")
	@Test(enabled = true)
	public void useCookieTest() throws Exception {
		getCookieTest();
		System.err.println("Getting the cookies");
		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Closing the browser");
		wait = null;
		System.err.println("re-open the browser, about to use the session cookies");
		driver.close();

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

		if (false) {
			System.setProperty("webdriver.gecko.driver",
					osName.equals("windows")
							? (new File("c:/java/selenium/geckodriver.exe")).getAbsolutePath()
							: Paths.get(System.getProperty("user.home")).resolve("Downloads")
									.resolve("geckodriver").toAbsolutePath().toString());
			/* DesiredCapabilities */ capabilities = DesiredCapabilities.firefox();
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
			profile.setAcceptUntrustedCertificates(true);
			profile.setAssumeUntrustedCertificateIssuer(true);

			// NOTE: ERROR StatusLogger No log4j2 configuration file found. Using
			// default configuration: logging only errors to the console.
			/* LoggingPreferences */ logPrefs = new LoggingPreferences();
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
			// System.err.println(System.getProperty("user.dir"));
			capabilities.setCapability(FirefoxDriver.PROFILE, profile);
			try {
				driver = new FirefoxDriver(capabilities);
			} catch (WebDriverException e) {
				e.printStackTrace();
				throw new RuntimeException(
						"Cannot initialize Firefox driver: " + e.toString());
			}
			// re-initialize wait object
			wait = new WebDriverWait(driver, flexibleWait);

			// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older
			// java has no precompiler #ifdef
			// wait.pollingEvery(Duration.ofMillis(pollingInterval));
			wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
			System.err.println("Navigating to " + landURL);
		}
		driver.get(landURL);
		sleep(10000);
		System.err.println("Loading cookies");
		for (Cookie cookie : cookies) {
			driver.manage().addCookie(cookie);
		}
		// org.openqa.selenium.InvalidCookieDomainException:
		driver.navigate().refresh();

		System.err.println("Waiting for inbox");
		try {
			wait.until(ExpectedConditions.urlContains("#inbox"));
		} catch (TimeoutException | UnreachableBrowserException e) {
			verificationErrors.append(e.toString());
		}
		doLogout();
	}

	private static void doLogout() {

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

	private static class BlockTestDialogEx extends IconAndMessageDialog {

		public static final int CONTINUE_ID = IDialogConstants.CLIENT_ID;
		public static final String CONTINUE_LABEL = "Continue";
		private Image image;
		private Label label;
		private String buttonText = "Continue";
		private String continueText = null;

		private String message = "Press button to continue Selenium test";

		public BlockTestDialogEx(Shell parent) {
			super(parent);

			try {
				image = new Image(parent.getDisplay(),
						new FileInputStream("src/main/resources/images/watchglass.png"));
			} catch (FileNotFoundException e) {
				System.err.println("Exception: " + e.toString());
			}
		}

		public void setMessage(String data) {
			this.message = data;
		}

		public void setButtonText(String data) {
			this.buttonText = data;
		}

		public void setContinueText(String data) {
			this.continueText = data;
		}

		public boolean close() {
			if (image != null)
				image.dispose();
			return super.close();
		}

		protected Control createDialogArea(Composite parent) {
			createMessageArea(parent);

			Composite composite = new Composite(parent, SWT.NONE);
			GridData data = new GridData(GridData.FILL_BOTH);
			data.horizontalSpan = 2;
			composite.setLayoutData(data);
			composite.setLayout(new FillLayout());

			label = new Label(composite, SWT.LEFT);
			label.setText(message);

			return composite;
		}

		protected void createButtonsForButtonBar(Composite parent) {
			Button button = createButton(parent, CONTINUE_ID, CONTINUE_LABEL, false);
			button.setText(buttonText);
		}

		protected void buttonPressed(int buttonId) {
			if (buttonId == CONTINUE_ID) {
				setReturnCode(buttonId);
				if (continueText != null) {
					System.err.println(continueText);
				}
				close();
			}
		}

		protected Image getImage() {
			return image;
		}
	}

	public void highlight(WebElement element) {
		highlight(element, 100, "solid yellow");
	}

	public void highlight(WebElement element, long highlightInterval) {
		highlight(element, highlightInterval, "solid yellow");

	}

	public void highlight(WebElement element, long highlightInterval,
			String color) {
		System.err.println("Color: " + color);
		if (wait == null) {
			wait = new WebDriverWait(driver, flexibleWait);
		}
		// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older
		// https://stackoverflow.com/questions/49687699/how-to-remove-deprecation-warning-on-timeout-and-polling-in-selenium-java-client
		wait.pollingEvery(Duration.ofMillis((int) pollingInterval));

		// wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);

		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			executeScript(String.format("arguments[0].style.border='3px %s'", color),
					element);
			Thread.sleep(highlightInterval);
			executeScript("arguments[0].style.border=''", element);
		} catch (InterruptedException e) {
			// System.err.println("Exception (ignored): " + e.toString());
		}
	}

	public void highlight(By locator) throws InterruptedException {
		highlight(locator, "solid yellow");
	}

	public void highlight(By locator, String color) throws InterruptedException {
		WebElement element = driver.findElement(locator);
		executeScript(String.format("arguments[0].style.border='3px %s'", color),
				element);
		Thread.sleep(highlightInterval);
		executeScript("arguments[0].style.border=''", element);
	}

	public Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			/*
			 *
			 // currently unsafe
			System.err.println(arguments.length + " arguments received.");
			String argStr = "";
			
			for (int i = 0; i < arguments.length; i++) {
				argStr = argStr + " "
						+ (arguments[i] == null ? "null" : arguments[i].toString());
			}
			
			System.err.println("Calling " + script.substring(0, 40)
					+ "..." + \n" + "with arguments: " + argStr);
					*/
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}

	// origin:
	// https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(name);
			if (value == null) {
				value = defaultValue;
			}
		}
		return value;
	}
}
