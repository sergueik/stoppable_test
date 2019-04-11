package com.github.sergueik.selenium_utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TestDialog extends IconAndMessageDialog {

	public static final int CONTINUE_ID = IDialogConstants.CLIENT_ID;
	public static final String CONTINUE_LABEL = "Continue";
	private Image image;
	private Label label;
	private String buttonText = "Continue";
	private String continueText = null;

	private String message = "Press button to continue Selenium test";

	public TestDialog(Shell parent) {
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