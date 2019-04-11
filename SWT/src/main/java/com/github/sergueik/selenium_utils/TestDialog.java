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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TestDialog extends IconAndMessageDialog {

	private static Display display = new Display();
	private static Shell shell;
	public static int maxCount = 10;
	public static final int CONTINUE_ID = IDialogConstants.CLIENT_ID;
	public static final String CONTINUE_LABEL = "Continue";
	private Image image;
	private Label label;
	private String buttonText = "Continue";
	private String continueText = null;
	public Button button;

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

	public void setMaxCount(int data) {
		this.maxCount = data;
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

		button = createButton(parent, CONTINUE_ID, CONTINUE_LABEL, false);
		button.setText(buttonText);
		getTask2(button, maxCount).start();

	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == CONTINUE_ID) {
			setReturnCode(buttonId);
			if (continueText != null) {
				System.err.println(continueText);
			}
			shell.close();
		}
	}

	public static Thread getTask2(Button button, int maxcount) {
		final Button _button = button;
		return new Thread() {
			public void run() {
				for (int cnt = 0; cnt != maxcount; cnt++) {
					final int count = cnt;
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
					}
					display.asyncExec(new Runnable() {
						public void run() {
							_button.setText("done:  " + count);
						}
					});
				}
				// prevent invalid thread access org.eclipse.swt.SWTException
				display.asyncExec(new Runnable() {
					public void run() {
						shell.close();
					}
				});

			}
		};
	}

	protected Image getImage() {
		return image;
	}

	public static void show(String message, int maxCount) {

		shell = new Shell(display);
		System.err.println("Hold the test");
		System.err.println("Creating new dialog on the display");
		TestDialog blockTestDialog = new TestDialog(shell);
		blockTestDialog.setMessage(message);
		blockTestDialog.setMaxCount(maxCount);
		blockTestDialog.setButtonText("Continue test");
		blockTestDialog.setContinueText("Continue the test");
		blockTestDialog.open();

		// timer
		// close();
	}
}