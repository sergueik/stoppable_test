### Info

This project demonstrates suspending Selenium test through an
[SWT](http://www.java2s.com/Tutorial/Java/0280__SWT/Catalog0280__SWT.htm) and [JavaFx](http://www.java2s.com/Tutorials/Java/JavaFX/index.htm) and [Swing](http://www.java2s.com/Tutorial/Java/0240__Swing/Catalog0240__Swing.htm) library dialog widgets.

A very basic dialog widget is sufficient for the cause, despite differences in the widget event loop processing
implementations under the hood of the three mainstream Java UI frameworks.

This technique can be used to post a plain Image Button Dialog to let the test be simply stopped /resumed or
through a commong custom dialog like one in [Swing Widgets](https://github.com/eugener/oxbow) or
[Nebula - Supplemental Widgets for SWT](https://github.com/eclipse/nebula)
to display the details of the test exception from the Junit or TestNg exception handler.

![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test.png)

The test is launched in  a usual fashion, via maven.
```cmd
mvn test
```
The examples in this directory borrows one of the test scenarios from another repository [selenium test](https://github.com/sergueik/selenium_tests). The test in question performs some scrolling actions in Chrome then gets stopped and the UI dialog is shown. It has been seen to successfully resume execution after 10...15 minutes of being paused. It is uncertain atm if the test would be able to continue after arbitrary long interruption.

### Note
Using Java for stop dialog guarantees platform independnce,
at a low cost that a different SWT jars need to be used for Mac OSX, Linux and Windows (32 or 64 bit).
The latter is easily solvable through property activated profiles, but may become a little of an extra challenge if the test suite needs maven profiles for its own purpose. The Swing and JavaFX might need similar platform-specific projects.

For JavaFx and Swing version even that isn't needed.

### See Also
  * [Eclipse Standard Widget Toolkit](https://www.eclipse.org/swt/)
  * [Tutorial for eclipse SWT and Jface dialogs](http://www.vogella.com/tutorials/EclipseDialogs/article.html)
  * [Swing Dialogs](https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html)

### License
This project is licensed under the terms of the MIT license.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
