### Info

This project demonstrates suspending Selenium test through an
[SWT](http://www.java2s.com/Tutorial/Java/0280__SWT/Catalog0280__SWT.htm) and [JavaFx](http://www.java2s.com/Tutorials/Java/JavaFX/index.htm) and [Swing](http://www.java2s.com/Tutorial/Java/0240__Swing/Catalog0240__Swing.htm) library dialog widgets.

A very basic dialog widget is sufficient for the cause, despite differences in the iunder the hood implementations of the widget event loop processing across three mainstream Java UI frameworks.

This technique can be used to post a plain Image Button Dialog to let the test be simply stopped /resumed or comunicate some information from the test through a common custom dialoss available in [Swing Widgets](https://github.com/eugener/oxbow) or [Nebula - Supplemental Widgets for SWT](https://github.com/eclipse/nebula) e.g. to print test exception stack from the Junit or testNg exception handler.

![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test-swt.png)

### Demo
The  example directory contsins the testNg test. The equivalent jUnit case is arriving shortly (not expected to lead to a difference in test behavior)
The test is launched in a usual fashion, via maven. Running from the project directory builds and tests all 3 frameworks one after another.
```cmd
mvn test
```
The examples in this directory borrows one of the test scenarios from another repository [selenium test](https://github.com/sergueik/selenium_tests). The test in question performs some scrolling actions in Chrome then gets stopped and the UI dialog is shown. It has been seen to successfully resume execution after 10...15 minutes of being paused. It is uncertain atm if the test would be able to continue after an arbitrary long interruption.

![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test-javafx.png)

### Note

Using Java for stop dialog guarantees platform independnce,
at a low cost that a different set of jars need to be used for Mac OSX, Linux and Windows (32 or 64 bit) for SWT.
This is easily solvable through property activated profiles, but may become a little of a challenge if the test suite itself needs maven profiles for some other purpose. The JavaFx and Swing versions have no such platformr-specific jar dependencies.

![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test-swing.png)

### See Also
  * [Eclipse Standard Widget Toolkit](https://www.eclipse.org/swt/)
  * [Tutorial for eclipse SWT and Jface dialogs](http://www.vogella.com/tutorials/EclipseDialogs/article.html)
  * [Swing Dialogs](https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html)

### License
This project is licensed under the terms of the MIT license.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
