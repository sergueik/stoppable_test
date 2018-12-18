package com.github.sergueik.selenium_utils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

// based on: http://www.java2s.com/Tutorial/Java/0240__Swing/SetDefaultCloseOperationforDialog.htm
// updating to hide the interim app
public class TestDialog extends JFrame {
  JDialog d = new JDialog(this, "Dialog title", true);

  public TestDialog() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    d.getContentPane().add(new JLabel("Click the OK button"), BorderLayout.CENTER);
    JButton closeIt = new JButton("OK");
    closeIt.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Closing dialog");
        d.dispose();
        d.setVisible(false);
        setVisible(false);
        System.out.println("Closed dialog - what now");      
        dispose();  
       }
    });
    d.getContentPane().add(closeIt, BorderLayout.SOUTH);
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