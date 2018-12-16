### Info

This project demonstrates suspending Selenium test through an 
[SWT](http://www.java2s.com/Tutorial/Java/0280__SWT/Catalog0280__SWT.htm) library dialog widget. Equivalent [Swing](http://www.java2s.com/Tutorial/Java/0240__Swing/Catalog0240__Swing.htm) and [JavaFx](http://www.java2s.com/Tutorials/Java/JavaFX/index.htm) varant is a work in progress - a very basic dialog widget is likely be sufficient for the cause (though the widget event loop is implemented differntly by the three).

![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test.png)

The test is launched in  a usual fashion, via maven.
```cmd
mvn test
```
The example in this directory borrows one of the test scenarios from another repository [selenium test](https://github.com/sergueik/selenium_tests). The test in question performs some scrolling actions in Chrome then gets stopped and the UI dialog is shown. It has been seen to successfully resume execution after 10...15 minutes of being paused. It is uncertain atm if the test would be able to continue after arbitrary long interruption. 

### Note
Using Java for stop dialog guarantees platform independnce, at a low cost that a different SWT jars need to be used for Mac OSX, Linux and Windows (32 or 64 bit), this is easily solvable through property activated profiles, but may become a little of an extra challenge if the test suite needs maven profiles for its own purpose. The Swing and JavaFX might need similar platform-specific projects.

### See Also
  * [Eclipse Standard Widget Toolkit](https://www.eclipse.org/swt/)

### License
This project is licensed under the terms of the MIT license.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
