package com.github.sergueik.selenium_utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("restriction")
public class TestDialog extends Application {

	private static String packagePath;
	private static Boolean debug = false;
	private static final String projectPrefix = "src/main/java";

	private static String resourcePath = "src/main/resources";
	private static final boolean useResourcePath = true;
	private static final String fillerMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation";

	private static int count = 0;
	private final Text text = new Text(Integer.toString(count));

	// The JavaFx controller class can be statically defined within another
	// make sure to ensure the "/*@fx:controller" attribute is defined correctly
	private String closeMessage = "Continue";

	private Map<String, String> inputData = new HashMap<>();

	public void setInputData(Map<String, String> inputData) {
		this.inputData = inputData;
		this.contentPreformatted.textProperty().set(inputData.get((Object) "code"));
		this.headerText.textProperty().set(inputData.get((Object) "header text"));
		this.contentSummary.textProperty()
				.set(inputData.get((Object) "summary message"));
		this.continueButton.textProperty()
				.set(inputData.get((Object) "button text"));
		this.closeMessage = inputData.get((Object) "close message");
	}

	public Map<String, String> getInputData() {
		return this.inputData;
	}

	private Stage mainStage;

	@FXML
	private Button continueButton;

	@FXML
	private Label headerText;

	@FXML
	private Label contentSummary;

	@FXML
	private Label contentPreformatted;

	private Parent fxmlEdit;

	private Stage stage;

	public void setMainStage(Stage mainStage) {
		this.mainStage = mainStage;
	}

	// TODO: on OSX
	// org.apache.maven.surefire.booter.SurefireBooterForkException: The forked
	// VM terminated without properly saying goodbye. VM crash or System.exit
	// called?
	@FXML
	private void initialize() {
		continueButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (closeMessage != null) {
					System.err.println(closeMessage);
				}
				Platform.exit();
			}
		});

	}

	private void decrementCount() {
		count--;
		if (count > 0) {
			text.setUserData(count);
			text.setText(String.format("%s %s", fillerMessage.substring(0, 3 * count),
					Integer.toString(count)));
		} else {
			Platform.exit();
			// System.exit(0);
		}
	}

	@Override
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();
		root.getChildren().add(text);

		Scene scene = new Scene(root, 200, 200);
		scene.getStylesheets()
				.add(
						"file:///"
								+ Paths.get(System.getProperty("user.dir"))
										.resolve(
												useResourcePath
														? String.format("%s/%s", resourcePath,
																"dialog_styles.css")
														: String.format("%s/%s/%s", projectPrefix,
																packagePath, "dialog_styles.css"))
										.toString().replace("\\", "/"));
		FXMLLoader fxmlLoader = new FXMLLoader();
		Map<String, String> data = new HashMap<>();
		data.put("title", "Selenium Dialog (WIP)");
		data.put("close message", "Abort dialog is closed");
		data.put("header text", "The test is being aborted");
		data.put("summary message", "Exception in the code");
		data.put("button text", "Abort");
		data.put("code",
				"Exception in Application start method\n"
						+ "java.lang.reflect.InvocationTargetException\n"
						+ "        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
						+ "        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n"
						+ "        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
						+ "        at java.lang.reflect.Method.invoke(Method.java:498)");

		// NOTE: Without the absolute path, would search current directory,
		// but appears to fail when run from sureFire
		packagePath = TestDialog.class.getPackage().getName().replace(".", "/");
		try {
			fxmlLoader.setLocation(
					new URL("file:///" + Paths.get(System.getProperty("user.dir"))
							.resolve(useResourcePath
									? String.format("%s/%s", resourcePath,
											"test_dialog_view.fxml")
									: String.format("%s/%s/%s", projectPrefix, packagePath,
											"test_dialog_view.fxml"))
							.toString()));
			Parent parent = fxmlLoader.load();
			// see also:
			// http://www.java2s.com/Code/Java/JavaFX/SetdialogModalityAPPLICATIONMODAL.htm
			// initModality(Modality.APPLICATION_MODAL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// longrunning operation runs on different thread
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				text.setOnMouseEntered(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						System.err.println(String.format("The text bounds: %.2f %.2f",
								Math.floor(text.getBoundsInLocal().getWidth()),
								Math.floor(text.getBoundsInLocal().getHeight())));

						text.setScaleX(1.5);
						text.setScaleY(1.5);
					}
				});

				text.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						text.setScaleX(1);
						text.setScaleY(1);
					}
				});

				Runnable updater = new Runnable() {

					@Override
					public void run() {
						decrementCount();
					}
				};

				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}

					// UI update is run on the Application thread
					Platform.runLater(updater);
				}
			}

		});
		// don't let thread prevent JVM shutdown
		thread.setDaemon(true);
		thread.start();

		Platform.runLater(() -> {
			primaryStage.setScene(scene);
			primaryStage.show();
		});

		/*
		    Stage myDialog = new MyDialog(primaryStage);
		    myDialog.sizeToScene();
		    myDialog.show();
		 */
	}

	public static void main(String[] args) {
		try {
			count = Integer.parseInt(args[0]);
		} catch (Exception e) {
			count = 5;
		}

		boolean useResourcePath = true;
		Platform.setImplicitExit(false);
		if (debug) {
			System.err
					.println("Loading from: " + Paths.get(System.getProperty("user.dir"))
							.resolve(useResourcePath
									? String.format("%s/%s", resourcePath,
											"test_dialog_view.fxml")
									: String.format("%s/%s/%s", projectPrefix, packagePath,
											"test_dialog_view.fxml"))
							.toString());
		}
		// java.lang.IllegalStateException: Application launch must not be called
		// more thanonce
		// https://stackoverflow.com/questions/24320014/how-to-call-launch-more-than-once-in-java
		launch(args);
	}

}
