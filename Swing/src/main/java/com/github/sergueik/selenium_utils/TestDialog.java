package com.github.sergueik.selenium_utils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

// based on: http://www.java2s.com/Tutorial/Java/0240__Swing/SetDefaultCloseOperationforDialog.htm
// updating to hide the interim app
// NOTE: the class is not currently used by StoppableTest:
// The inner static private static class TestDialog is used instead
public class TestDialog extends JFrame {
	JDialog d = new JDialog(this, "Dialog title", true);

	public TestDialog() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		d.getContentPane().add(new JLabel("Click the OK button"),
				BorderLayout.CENTER);
		JButton closeIt = new JButton("OK");

		closeIt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.err.println("Closing dialog");
				d.dispose();
				d.setVisible(false);
				setVisible(false);
				System.err.println("Closed dialog - what now");
				dispose();
			}
		});
		d.getContentPane().add(closeIt, BorderLayout.SOUTH);
		Icon watchglassIcon = new ImageIcon(
				String.format("%s/src/main/resources/images/watchglass.png",
						System.getProperty("user.dir")));
		JButton watchglassJbutton = new JButton(watchglassIcon);
		System.err.println("Icon width = " + watchglassJbutton.getWidth());
		System.err.println("Icon height = " + watchglassJbutton.getHeight());
		d.getContentPane().add(watchglassJbutton, BorderLayout.NORTH);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		d.pack();

		getContentPane().add(new JLabel("Placeholder label"));
		pack();
		setSize(200, 200);
		setVisible(false);
		d.setVisible(true);
	}

	public static void main(String[] args) {
		new TestDialog();
	}
}