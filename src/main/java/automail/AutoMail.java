package automail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AutoMail implements RequestHandler<Object, Object> {
	@SuppressWarnings("deprecation")
	public Object handleRequest(Object input, Context context) {

		// parse received json and store parameters in separate variables
		JSONObject jsonObject = (JSONObject) input;
		JSONArray query = jsonObject.getJSONArray("q");
		JSONObject queryJson = (JSONObject) query.get(0);

		JSONArray receiverArray = queryJson.getJSONArray("receiver");
		String receiver = receiverArray.getString(0);

		JSONArray subjectArray = queryJson.getJSONArray("subject");
		String subject = subjectArray.getString(0);

		JSONArray bodyArray = queryJson.getJSONArray("message");
		String message = bodyArray.getString(0);

		// check the received input
		System.out.println("received these inputs : " + receiver + " " + subject + " " + message);

		/*
		 * -> download the driver matching your system browser and place it at project's
		 * root if using the below syntax else use
		 * System.setProperty("webdriver.chrome.driver",<driver's absolute path>);
		 * 
		 */
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

		WebDriver driver = new ChromeDriver(); // initialize driver instance

		// with the received params, sending mail using selenium
		// go to official outlook site
		driver.get("https://www.outlook.com/");

		// automation logic of sending mail starts
		// --------------------------------------------------

		// store identifiers to locate web elements.
		// AbsXPath is absolute xpath, RelXPath is relative xpath
		final String signInButtonAbsXPath = "/html/body/header/div/aside/div/nav/ul/li[2]/a";
		final String emailBoxAbsXPath = "/html/body/div/form[1]/div/div/div[2]/div[1]/div/div/div/div/div[1]/div[3]/div/div/div/div[4]/div/div/div/div/input";
		final String emailBoxRelXPath = "//*[@id=\"i0116\"]";
		final String emailNextButtonRelXPath = "//*[@id=\"idSIButton9\"]";
		final String passwordBoxAbsXPath = "/html/body/div/form[1]/div/div/div[2]/div[1]/div/div/div/div/div/div[3]/div/div[2]/div/div[3]/div/div[2]/input";
		final String passwordNextAbsXPath = "/html/body/div/form[1]/div/div/div[2]/div[1]/div/div/div/div/div/div[3]/div/div[2]/div/div[4]/div[2]/div/div/div/div/input";
		final String staySignedInTestAbsXPath = "/html/body/div/form/div/div/div[2]/div[1]/div/div/div/div/div/div[3]/div/div[2]/div/div[1]";
		final String staySignedInNoButton = "//*[@id=\"idBtn_Back\"]";
		final String newMessageButtonClassName = "_9fiU2J67uJPVP0DBdOFMW";
		final String receiverBoxRelXPath = "//*[@id=\"ReadingPaneContainerId\"]/div/div/div/div[1]/div[1]/div[1]/div/div[1]/div/div/div/div/div[1]/div/div/input";
		final String receiverBoxClassName = "ms-BasePicker-input";
		final String subjectBoxClassName = "ms-TextField-field";
		final String messageBoxClassName = "_16VySYOFix816mo3KsgOhw";

		// create an instance of WebDriverWait set to 10 seconds max wait time
		WebDriverWait wait = new WebDriverWait(driver, 10);

		/*
		 * wait for sign in button to appear
		 * 
		 * use wait instance created earlier to wait for a max of 10 seconds for sign in
		 * button to appear on the page (assuming slow connection) else TimeoutException
		 * will be thrown
		 * 
		 * use xpath to locate sign in button
		 */
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(signInButtonAbsXPath)));

		/*
		 * locate sign in button ,then call click method on that element(button)
		 * 
		 * -> used xpath to locate the sign button. id, name, className ..etc can also
		 * be used to locate elements on the webpage note : when one doesn't work , use
		 * any other from the remaining ones.
		 */
		WebElement signIn = driver.findElement(By.xpath(signInButtonAbsXPath));
		// click signIn button
		signIn.click();
		// store email account creds in a variable for testing
		String email = "username"; // use your outlook username
		String password = "password"; // use your outlook password

		// wait for email box to appear . reusing the wait created earlier which is set
		// to wait for a max of 10 seconds
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(emailBoxAbsXPath)));
		// find the email box and pass the email in the box
		driver.findElement(By.xpath(emailBoxRelXPath)).sendKeys(email);
		// click next button
		driver.findElement(By.xpath(emailNextButtonRelXPath)).click();

		// wait for password box to appear
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(passwordBoxAbsXPath)));
		// pass the password in the box
		driver.findElement(By.xpath(passwordBoxAbsXPath)).sendKeys(password);
		// click next button to login
		driver.findElement(By.xpath(passwordNextAbsXPath)).click();

		// if "stay signed in ?"page appears then click "No" and proceed further, else
		// just display a mssg saying it didnt appear
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(staySignedInTestAbsXPath)));
			driver.findElement(By.xpath(staySignedInNoButton)).click();
		} catch (NoSuchElementException e) {
			System.out.println("'Stay Signed in ?' page didn't appear");
			e.printStackTrace(); // for details on exception
		}

		// wait for New message button to appear
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className(newMessageButtonClassName)));
		// click New message button
		driver.findElement(By.className(newMessageButtonClassName)).click();

		// ------------------ drafting email to be sent using parameters received from
		// json object ------------------------
		// wait for the receiver box to appear
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(receiverBoxRelXPath)));
		/*
		 * enter the receiver mail id in the box
		 * 
		 * note : if u get class names as "class1 class2 class3" , then using only first
		 * one will be just fine. -> compound class name is not allowed as parameter
		 */
		driver.findElement(By.className(receiverBoxClassName)).sendKeys(receiver);
		// enter the subject
		driver.findElement(By.className(subjectBoxClassName)).sendKeys(subject);
		// enter the body
		driver.findElement(By.className(messageBoxClassName)).sendKeys(message);

		// Take the screenshot of page, this will be useful when using headless version
		// of chromium, where browser is operated in the background
		// to confirm we drafted the mail properly before send it.
		TakesScreenshot screenshot = (TakesScreenshot) driver;
		// Saving the screenshot in desired location
		File source = screenshot.getScreenshotAs(OutputType.FILE);
		// Path to the location to save screenshot
		try {
			FileUtils.copyFile(source, new File("./SeleniumScreenshots/Screen.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Screenshot is captured");

		/*
		 * leaving the logic to click the send button to send the mail. can be
		 * implemented if desired
		 * 
		 * Note : This demonstration was to explore few functionalities of selenium .
		 * overloading servers with unusual traffic using frameworks like selenium is
		 * not intended in any way.
		 */

		driver.quit();

		return null;
	}
}
