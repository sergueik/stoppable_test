package com.github.sergueik.selenium_utils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
// import javax.swing.JLabel;
import javax.swing.WindowConstants;

// based on:
// http://www.java2s.com/Tutorial/Java/0240__Swing/SetDefaultCloseOperationforDialog.htm
// https://stackoverflow.com/questions/2713190/how-to-remove-border-around-buttons
// NOTE: the class was extracted from originally an static inner private class inside the test example StoppableTest
@SuppressWarnings("serial")
public class TestDialog extends JFrame {

	private final boolean debug = false;
	private final int width = 400;
	private final int height = 250;
	private final int borderWidth = 40;
	private final int imageIconEmptyBorderWidth = 4;
	JDialog jDialog = new JDialog(this, "Selenium test stopped", true);

	public TestDialog() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Icon watchglassIcon = new ImageIcon(
				String.format("%s/src/main/resources/images/watchglass.png",
						System.getProperty("user.dir")));
		JButton watchglassJbutton = new JButton(watchglassIcon);
		watchglassJbutton.setBorderPainted(false);
		watchglassJbutton.setBorder(BorderFactory.createEmptyBorder(
				imageIconEmptyBorderWidth, imageIconEmptyBorderWidth,
				imageIconEmptyBorderWidth, imageIconEmptyBorderWidth));
		watchglassJbutton.setContentAreaFilled(false);

		jDialog.getContentPane().add(watchglassJbutton, BorderLayout.CENTER);

		JButton continueButton = new JButton("Continue");
		continueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (debug) {
					System.err.println("Closing dialog");
				}
				jDialog.dispose();
				jDialog.setVisible(false);
				setVisible(false);
				if (debug) {
					System.err.println("Closed dialog.");
				}
				dispose();
			}
		});
		jDialog.getContentPane().add(continueButton, BorderLayout.SOUTH);
		jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(borderWidth,
				borderWidth, borderWidth, borderWidth));
		jDialog.pack();
		// TODO: timer
		setSize(width, height);
		jDialog.setVisible(true);
	}

	public static void main(String[] args) {
		new TestDialog();
	}
}