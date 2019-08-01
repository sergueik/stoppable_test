### Info

This project demonstrates pausing the Selenium test through an modal dialog which is available or can be easily composed from library dialog widgets in all common java GUI frameworks:
  * [Eclipse Standard Widget Toolkit](https://www.eclipse.org/swt/) and its [WindowBuider](https://www.eclipse.org/windowbuilder/)
  * [JavaFx](https://openjfx.io/)
  * [Swing Framework](https://docs.oracle.com/javase/8/docs/api/index.html?javax/swing/package-summary.html) with [GUI Designer](https://marketplace.eclipse.org/content/jformdesigner-swing-gui-designer#group-screenshots) or [JForm Designer](https://www.formdev.com/jformdesigner/download/)

The most basic dialog widget is sufficient for the cause, despite slight differences in the under the hood implementations of the widget event loop processing by these mainstream Java UI frameworks.

This technique can be used to add an Image Button Dialog and text to communicate  the name of the test method that is s paused. Alternatively one may add text box or an accordion widget to comunicate some extra information from the test through one of common custom dialogs available in [Swing Widgets](https://github.com/eugener/oxbow) or [Nebula - Supplemental Widgets for SWT](https://github.com/eclipse/nebula) e.g. to print test exception stack from the Junit or testNg exception handler.

![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test-swt.png)

### Demo
The  example directory contains an testNg test. The equivalent jUnit case is arriving shortly and isn't expected to lead to a difference in test behavior.
The test is launched in a usual fashion, via maven. Running from the project directory builds and tests all 3 frameworks one after another.
```cmd
mvn test
```
The examples in this directory borrows one of the test scenarios from another repository [selenium test](https://github.com/sergueik/selenium_tests). The test in question performs some scrolling actions in Chrome then gets stopped and the UI dialog is shown. It has been seen to successfully resume execution after 10...15 minutes of being paused. It is uncertain atm if the test would be able to continue after an arbitrary long interruption.

![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test-javafx.png)

### Note

Using Java for stop dialog guarantees platform independnce,
at a low cost that a different set of jars need to be used for Mac OSX, Linux and Windows (32 or 64 bit) for SWT.
This is easily solvable through property activated profiles, but may become a little of a challenge if the test suite itself needs maven profiles for some other purpose.
The JavaFx and Swing versions have no such platformr-specific jar dependencies.

![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test-swing.png)

### Note

On older mac / hackintosh platforms one need to pick the ChromeDriver version matching whatever Chrome version browser would be installed, in paricular
Google Chrome build 49.0.2623.112
for mac is a 32-bit app and requires a 32 bit chromdriver build 22  to even work.
The supported versions are always listed in the `notes.txt` e.g.
```sh
https://chromedriver.storage.googleapis.com/2.22/notes.txt
```

Using the correct combination allows driver to at least make the browser visible on the desktop but may still fail to navigate anywhere
from the starting page `data:,` and test eventually timing out (one can add assertions).

Using a mismatched combination is likely lead to the exception
```java
org.openqa.selenium.WebDriverException:
Timed out waiting for driver server to start.
```
No such problem with (also old) build of Firefox and the geckodriver
![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test-swing_hackintosh.png)

### See Also
  * [Eclipse SWT](http://www.java2s.com/Tutorial/Java/0280__SWT/Catalog0280__SWT.htm) tutorial on java2s
  * [JavaFX](http://www.java2s.com/Tutorials/Java/JavaFX/index.htm) tutorial on java2s
  * [Swing](http://www.java2s.com/Tutorial/Java/0240__Swing/Catalog0240__Swing.htm) tutorial on java2s
  * [JavaFX overview](https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm)
  * [Tutorial for eclipse SWT and Jface dialogs](http://www.vogella.com/tutorials/EclipseDialogs/article.html)
  * [core Swing Dialogs](https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html)
  * [Swing Tutotial](http://zetcode.com/tutorials/javaswingtutorial/)
  * [collection](https://github.com/aterai/java-swing-tips) of core Java Swing GUI programs with small source code examples

### License
This project is licensed under the terms of the MIT license.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
