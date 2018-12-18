package com.github.sergueik.selenium_utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

// origin: 
public class AboutDialog extends JDialog implements ActionListener {
  public AboutDialog(JFrame parent, String title, String message) {
    super(parent, title, true);
    if (parent != null) {
      Dimension parentSize = parent.getSize(); 
      Point p = parent.getLocation(); 
      setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
    }
    JPanel messagePane = new JPanel();
    messagePane.add(new JLabel(message));
    getContentPane().add(messagePane);
    JPanel buttonPane = new JPanel();
    JButton button = new JButton("OK"); 
    buttonPane.add(button); 
    button.addActionListener(this);
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    // setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    pack(); 
    setVisible(true);
  }
  public void actionPerformed(ActionEvent e) {
    System.err.println("Closing");
    setVisible(false); 
    System.err.println("Disposing");
    dispose();
    return;
  }
  public static void main(String[] a) {
    AboutDialog dlg = new AboutDialog(new JFrame(), "title", "message");
    System.err.println("Closed");
    // TODO: not returning control to the shell.
    return;
  }
}