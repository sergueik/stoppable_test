### Info
This project demonstrates suspending Selenium test through a SWT dialog.
![icon](https://github.com/sergueik/stoppable_test/blob/master/screenshots/capture_stopped_test.png)
### The test is launched in  a usual fashion, via maven.
```cmd
mvn test
```
The example in this directory borrows one of the test scenarios from another repository [selenium test](https://github.com/sergueik/selenium_tests).
The test in question performs some scrolling actions in Chrome then stops and the UI dialog is shown. It has been testes to successfully resume execution after 10...15 minutes of being paused. It is unclear atm if the test would be able to continue after arbitratily long interruption. The Swing and JavaFx varant is a work in progress (just  very basic dialog widget is likely be sufficient for the cause).

### Note
Using Java for stop dialog guarantees the platform independnce, at a low cost that a different SWT jars need to be used for Mac OSX, Linux and Windows (32 or 64 bit), this is easily solvable through property activated profiles, but may become a little of an extra challenge if the test suite is already using maven profiles for some other purpose.

### License
This project is licensed under the terms of the MIT license.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
