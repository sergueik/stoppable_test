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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.WindowConstants;

// based on:
// http://www.java2s.com/Tutorial/Java/0240__Swing/SetDefaultCloseOperationforDialog.htm
// https://stackoverflow.com/questions/2713190/how-to-remove-border-around-buttons
// NOTE: the class was extracted from originally an static inner private class inside the test example StoppableTest
@SuppressWarnings("serial")
public class TestDialog extends JFrame {

	private String callerText = null;
	private final boolean debug = false;
	private final int width = 400;
	private final int height = 250;
	private final int borderWidth = 40;
	private final int imageIconEmptyBorderWidth = 4;
	JDialog jDialog = null;

	private void paintTestDialog() {
		jDialog = new JDialog(this, String.format("Selenium test %s stopped",
				String.format("%s\u2026", callerText)), true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Icon watchglassIcon = new ImageIcon(
				String.format("%s/src/main/resources/images/watchglass.png",
						System.getProperty("user.dir")));
		JButton watchglassJbutton = new JButton(watchglassIcon);
		// http://www.java2s.com/Tutorial/Java/0240__Swing/ButtonwithuserdrawImageicon.htm
		repaint();
		watchglassJbutton.setBorderPainted(false);
		watchglassJbutton.setBorder(BorderFactory.createEmptyBorder(
				imageIconEmptyBorderWidth, imageIconEmptyBorderWidth,
				imageIconEmptyBorderWidth, imageIconEmptyBorderWidth));
		watchglassJbutton.setContentAreaFilled(false);

		jDialog.getContentPane().add(watchglassJbutton, BorderLayout.CENTER);

		// custom icon
		JButton continueButton = new JButton(
				String.format("Continue Test %s", callerText),
				new OwnDrawnBoxIcon(Color.blue, 1));
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

	public TestDialog(String text) {
		this.callerText = text;
		paintTestDialog();
	}

	public TestDialog() {
		this.callerText = "unknown";
		paintTestDialog();

	}

	public static void main(String[] args) {
		new TestDialog();
	}

	private static class OwnDrawnBoxIcon implements Icon {
		private Color color;

		private int borderWidth;

		OwnDrawnBoxIcon(java.awt.Color color, int borderWidth) {
			this.color = color;
			this.borderWidth = borderWidth;
		}

		public int getIconWidth() {
			return 20;
		}

		public int getIconHeight() {
			return 20;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.black);
			g.fillRect(x, y, getIconWidth(), getIconHeight());
			g.setColor(color);
			g.fillRect(x + borderWidth, y + borderWidth,
					getIconWidth() - 2 * borderWidth, getIconHeight() - 2 * borderWidth);
		}
	}
}