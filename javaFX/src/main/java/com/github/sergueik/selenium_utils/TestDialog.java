package com.github.sergueik.selenium_utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

@SuppressWarnings("restriction")
// https://stackoverflow.com/questions/13015537/javafx-class-controller-stage-window-reference/13022321
public class TestDialog extends Application {

	
	private static String packagePath;
	private static Boolean debug = false;
	private static final String projectPrefix = "src/main/java";

	private static String resourcePath = "src/main/resources";
	private static final boolean useResourcePath = true;
	private static int count = 0;
	private final Text text = new Text(Integer.toString(count));

	private Map<String, String> inputData = new HashMap<>();

	public Map<String, String> getInputData() {
		return this.inputData;
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

		// long running operation is to be run on different thread
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				Runnable updater = new Runnable() {

					@Override
					public void run() {
						count--;
						if (count > 0) {
							// https://www.programcreek.com/java-api-examples/index.php?api=javafx.scene.control.OverrunStyle
							// text.setTextOverrun(OverrunStyle.ELLIPSIS);
							text.setText(Integer.toString(count));
							text.setUserData(count);
						} else {
							scene.getRoot().setVisible(false);
							primaryStage.hide();
							done = true;
							// does not return from launch() unless the next line is called.
							Platform.exit();
							// System.exit(0);
						}
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
		// thread.setDaemon(true);
		thread.start();

		Platform.runLater(() -> {
			primaryStage.setScene(scene);
			primaryStage.show();
		});

	}

	public static void main1(String[] args) {
		try {
			count = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			count = 5;
		}

		boolean useResourcePath = true;
		// opposite of
		// https://stackoverflow.com/questions/24320014/how-to-call-launch-more-than-once-in-java
		Platform.setImplicitExit(true);
		// https://stackoverflow.com/questions/24320014/how-to-call-launch-more-than-once-in-java
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
		// java.lang.IllegalStateException:
		// Application launch must not be called more than once
		// https://stackoverflow.com/questions/24320014/how-to-call-launch-more-than-once-in-java
		launch(args);
	}

	private static boolean done = false;

	public static void main2(String[] args) {
		Platform.setImplicitExit(false);
		try {
			count = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			count = 5;
		}
		// java.lang.IllegalStateException:
		// Not on FX application thread; currentThread = main
		// new TestDialog().start(new Stage());
		done = false;
		// https://stackoverflow.com/questions/22772379/updating-ui-from-different-threads-in-javafx
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// Your class that extends Application
				new TestDialog().start(new Stage());
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}
				}
			}
		});
		while (!done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
