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
import javax.swing.Timer;

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

	private boolean closeOnTimeout = false;
	private Timer timer;
	private int interval = 60;
	private int count = 10;

	private String callerText = "";
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
		if (closeOnTimeout) {
			timer = new Timer(interval * 1000, e -> {
				if (count > 0) {
					System.err.println(String.format("Auto-close in %d min", count));
					continueButton.setVisible(false);
					continueButton.setText(
							String.format("Auto-close in %s min", String.valueOf(count--)));
					continueButton.doLayout();
					continueButton.setVisible(true);
				} else {
					((Timer) (e.getSource())).stop();
					/*
					continueButton.setVisible(false);
					continueButton.setText("Click me");
					continueButton.doLayout();
					continueButton.setVisible(true);
					continueButton.setEnabled(true);
					*/
					if (debug) {
						System.err
								.println("Automatically closing dialog " + " - timer expired");
					}
					jDialog.dispose();
					jDialog.setVisible(false);
					setVisible(false);
					if (debug) {
						System.err.println("Closed the dialog.");
					}
					dispose();

				}
			});
			timer.setInitialDelay(0);
		}
		jDialog.getContentPane().add(continueButton, BorderLayout.SOUTH);
		jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(borderWidth,
				borderWidth, borderWidth, borderWidth));
		jDialog.pack();

		setSize(width, height);
		if (closeOnTimeout) {
			timer.start();
		}
		jDialog.setVisible(true);
	}

	public TestDialog(String callerText) {
		this.callerText = callerText;
		paintTestDialog();
	}

	public TestDialog(String callerText,
			boolean closeOnTimeout) {
		this.callerText = callerText;
		this.closeOnTimeout = closeOnTimeout;
		paintTestDialog();
	}

	public TestDialog(String callerText, boolean closeOnTimeout,
			int count) {
		this.callerText = callerText;
		this.closeOnTimeout = closeOnTimeout;
		this.count = count;
		paintTestDialog();
	}

	public TestDialog() {
		paintTestDialog();
	}

	public static void main(String[] args) {
		new TestDialog("main");
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