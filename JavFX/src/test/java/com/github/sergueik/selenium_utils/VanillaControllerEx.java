package com.github.sergueik.selenium_utils;

import javafx.collections.ObservableList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

@SuppressWarnings({ "unused", "restriction" })
public class VanillaControllerEx {

	private String closeMessage = "Continue";

	private Map<String, String> inputData = new HashMap<>();

	public void setInputData(Map<String, String> inputData) {
		this.inputData = inputData;
		this.contentPreformatted.textProperty().set(inputData.get((Object) "code"));
		this.headerText.textProperty().set(inputData.get((Object) "header text"));
		this.contentSummary.textProperty().set(inputData.get((Object) "summary message"));
		this.continueButton.textProperty().set(inputData.get((Object) "continue button text"));
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
}
