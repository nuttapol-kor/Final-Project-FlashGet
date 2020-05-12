# FlashGet
FlashGet is an application to download file from URL in high-speed download by using multithreaded.

-----

# Usage
Change directory to flashget.jar file location and use this command
```bash
java -jar flashget.jar
```
For Java 11 or new version you need to specify the *module path* for JavaFX.
```bash
java --module-path {module javaFX path} --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar flashget.jar
```

**Ex.**
```bash
java --module-path C:\Users\USER\Documents\lib\javafx-sdk-11.0.2\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar flashget.jar
```

![Imgur](https://i.imgur.com/hP35RIJ.jpg)

----

# Interface

![Imgur](https://i.imgur.com/P3cOZqL.jpg)
- 1 is the text field to input the URL to download.
- 2 is a download button. If the text field is empty it does not do anything, when the user put an invalid URL or that URL cannot download it will show a dialog to change the URL. If it can download user can choose the directory path to save a file.
- 3 is a clear button to clear the input and all progress bars.
- 4 is the option to use thread for download
    - Auto Thread means the program will choose the number of threads for download.
    - Custom Thread means the user can choose the number of threads for download.
    - Combobox will show when the user clicks the custom thread option only.
- 5 will show when user click the download button and input URL can download, it shows file name and process.
- 6 is a cancel button to cancel the download process.
- 7 show all threads progress bar that use to download.

-----
# UML Diagram
![Imgur](https://i.imgur.com/SBZUX8k.png)

----
## Features
-  Graphical UI where the user can choose a directory where to save the download.
-  Before download, the user can check how large is the file he wants to download.
-  Progress bars show download progress.
-  Option to use thread for download

## Technology Used
-  Interface to shown used JavaFX and SceneBuilder
-  Used css file to styling the GUI

## Pattern
-  command pattern
-  observer pattern

## Education Value
- Learn how to use threads and random access file I/O.
- Learn how to create a GUI with JavaFX.
- Learn to use SceneBuilder
- Learn to use css styling the GUI