package flashget;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller to control GUL
 *
 * @author Nuttapol Korcharoenrat 6210546404.
 */
public class Controller {

    @FXML
    private AnchorPane root;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField urlField;

    @FXML
    private Button downloadButton;

    @FXML
    private Button clearButton;

    @FXML
    private Label filenameLabel;

    @FXML
    private Label label;

    @FXML
    private Button cancelButton;


    @FXML
    private RadioButton autoOption;

    @FXML
    private ToggleGroup threadOption;

    @FXML
    private RadioButton customOption;

    @FXML
    private ComboBox<Integer> nThreadBox;

    private ObservableList<Integer> threadNum = FXCollections.observableArrayList(1,2,3,4,5);

    @FXML
    private Label threadLabel;

    @FXML
    private ProgressBar threadProgressBar1;

    @FXML
    private ProgressBar threadProgressBar2;

    @FXML
    private ProgressBar threadProgressBar3;

    @FXML
    private ProgressBar threadProgressBar4;

    @FXML
    private ProgressBar threadProgressBar5;

    private File initPath = new File(System.getProperty("user.home"));

    private Task<Long>[] task;

    private long fileLength;

    private long fileReach = 0;

    private boolean isAutoThread;
    /**
     * Set action to all button
     */
    @FXML
    public void initialize() {
        downloadButton.setOnAction(this::downloadPressed);
        clearButton.setOnAction(this::clearPressed);
        cancelButton.setOnAction(this::cancelPressed);
        progressBar.setProgress(0.0);
        nThreadBox.setItems(threadNum);
        nThreadBox.getSelectionModel().selectFirst();
        autoOption.setOnAction(this::radioButtonPressed);
        customOption.setOnAction(this::radioButtonPressed);
    }

    /**
     * Download Handler
     * Download and write a file from url in textfield if that url can connect
     * If it not, show dialog and not do anything.
     *
     * @param event event when user pressed the download button.
     */
    public void downloadPressed(ActionEvent event) {
        // get text from text field
        String text = urlField.getText().trim();
        // file name will show in the last index of "/"
        // ex. https://skeoop.github.io/assignments/PA4-Threaded-Downloader.pdf file name : PA4-Threaded-Downloader.pdf
        String filename = text.substring(text.lastIndexOf("/") + 1);
        // set file name in Label
        filenameLabel.setText(filename);
        URL url = null;
        // if text field is empty do noting.
        if (text.isEmpty()) return;
        // visible all progress
        threadProgressBar1.setVisible(false);
        threadProgressBar2.setVisible(false);
        threadProgressBar3.setVisible(false);
        threadProgressBar4.setVisible(false);
        threadProgressBar5.setVisible(false);
        threadLabel.setVisible(false);
        // set start
        this.fileLength = 0;
        // change string in text field to URL.
        try {
            url = new URL(text);
            URLConnection connection = url.openConnection();
            this.fileLength = connection.getContentLengthLong();
        } catch (MalformedURLException ex) {
            // if cannot connect print no protocol in console
            System.err.println(ex.getMessage());
        } catch (IOException ioe) {
            System.out.println("Have some error in getContentLengthLong");
        }
        // if url can download file length will > 0
        if (this.fileLength > 0) {
            // reset file reach
            this.fileReach = 0;
            // can download.
            File file = fileChooser(filename);
            if (file == null) {
                // user pressed cancel button in file chooser
                return; // leave
            }
            if (isAutoThread) { // pressed auto
                if (this.fileLength < 50_000_000) {
                    managingThread(url, file, this.fileLength, 1);
                } else {
                    managingThread(url, file, this.fileLength, 5);
                }
            } else { // pressed custom
                managingThread(url, file, this.fileLength, nThreadBox.getValue());
            }
            // show progress bar
            progressBar.setVisible(true);
            // set file name in label
            filenameLabel.setText(file.getName());
            // show file name label
            filenameLabel.setVisible(true);
            // show byte downloaded
            label.setVisible(true);
            // show cancel button
            cancelButton.setVisible(true);
        } else { // if url cannot download show dialog.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("Your URL can not download");
            alert.setContentText("Please change your URL");
            alert.showAndWait();
        }
    }

    /**
     * Clear Handler
     * Clear text in text field and invisible all progressbar, cancel button and some label
     *
     * @param event event when user pressed clear button.
     */
    public void clearPressed(ActionEvent event) {
        urlField.clear();
        filenameLabel.setVisible(false);
        progressBar.setVisible(false);
        label.setVisible(false);
        cancelButton.setVisible(false);
        threadLabel.setVisible(false);
        threadProgressBar1.setVisible(false);
        threadProgressBar2.setVisible(false);
        threadProgressBar3.setVisible(false);
        threadProgressBar4.setVisible(false);
        threadProgressBar5.setVisible(false);
    }

    /**
     * Cancel Handler
     * Cancel all task
     *
     * @param event event when user pressed cancel button.
     */
    public void cancelPressed(ActionEvent event) {
        for (Task<Long> eachTask : task) {
            eachTask.cancel();
        }
    }

    /**
     * Download method used DownloadTask class to download file from url by n thread.
     *
     * @param url     url to download
     * @param file    file to write
     * @param size    size of file
     * @param nThread number of thread use to download
     */
    public void managingThread(URL url, File file, long size, int nThread) {
        // fixed pool to use the thread.
        ExecutorService executor = Executors.newFixedThreadPool(nThread);
        // create task array to store n task.
        this.task = new DownloadTask[nThread];
        // each thread downloads part of the file. The part sizes should be multiples of 4KB (4,096 bytes),
        // because Windows and Linux file space is allocated and written in blocks of size 4KB.
        final long KB = 4_096 * 4;
        // chunk to separate the tasks to download
        long chunk = (long) Math.ceil((double) size / (double) KB);
        // chunk size for each task
        long chunkSize = (chunk / nThread) * KB;
        // crated progressBar list to use in a loop

        List<ProgressBar> progressBarList = new ArrayList<>();
        progressBarList.add(threadProgressBar1);
        progressBarList.add(threadProgressBar2);
        progressBarList.add(threadProgressBar3);
        progressBarList.add(threadProgressBar4);
        progressBarList.add(threadProgressBar5);
        // show tread label
        threadLabel.setVisible(true);
        // loop to separate the n task to download
        for (int i = 0; i < nThread; i++) {
            task[i] = new DownloadTask(url, file, chunkSize * i, chunkSize);
            if ((i + 1) == nThread) {
                task[i] = new DownloadTask(url, file, chunkSize * i, size - (chunkSize * i));
            }
            // update progress bar for each task
            progressBarList.get(i).progressProperty().bind(task[i].progressProperty());
            // add listener to each task to show progress label of all task
            task[i].valueProperty().addListener(this::byteReadUpdate);
            // run the task
            executor.execute(task[i]);
            // show progress bar for each task
            progressBarList.get(i).setVisible(true);
        }
        // condition to update the main progress bar.
        if (nThread == 1) {
            progressBar.progressProperty().bind(task[0].progressProperty());
        } else if (nThread == 2) {
            progressBar.progressProperty().bind(task[0].progressProperty().multiply(0.5).add(task[1].progressProperty().multiply(0.5)));
        } else if (nThread == 3) {
            progressBar.progressProperty().bind(task[0].progressProperty().multiply(1.0 / 3.0).add(task[1].progressProperty().multiply(1.0 / 3.0)).add(task[2].progressProperty().multiply(1.0 / 3.0)));
        } else if (nThread == 4) {
            progressBar.progressProperty().bind(task[0].progressProperty().multiply(0.25).add(task[1].progressProperty().multiply(0.25)).add(task[2].progressProperty().multiply(0.25)).add(task[3].progressProperty().multiply(0.25)));
        } else if (nThread == 5) {
            progressBar.progressProperty().bind(task[0].progressProperty().multiply(0.2).add(task[1].progressProperty().multiply(0.2)).add(task[2].progressProperty().multiply(0.2)).add(task[3].progressProperty().multiply(0.2)).add(task[4].progressProperty().multiply(0.2)));
        }
        // shutdown all task when finish the work
        executor.shutdown();
    }

    /**
     * Listener to update progress for task
     *
     * @param subject  Observable
     * @param oldValue old value
     * @param newValue new value
     */
    public void byteReadUpdate(ObservableValue<? extends Long> subject, Long oldValue, Long newValue) {
        if (oldValue == null) oldValue = 0L;
        this.fileReach += (newValue - oldValue);
        String progress = String.format("%d/%d", this.fileReach, this.fileLength);
        label.setText(progress);
    }

    /**
     * File chooser to create file and save in any path.
     *
     * @param filename init file name.
     * @return created file.
     */
    public File fileChooser(String filename) {
        final FileChooser fileChooser = new FileChooser();
        if (this.initPath != null) {
            fileChooser.setInitialDirectory(this.initPath);
        }
        Stage stage = (Stage) root.getScene().getWindow();
        String ext = filename.substring(filename.lastIndexOf("."));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File type", "*" + ext));
        fileChooser.setInitialFileName(filename);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            this.initPath = file.getParentFile();
        }
        return file;
    }

    public void radioButtonPressed(ActionEvent event) {
        if (autoOption.isSelected()) {
            isAutoThread = true;
            nThreadBox.setVisible(false);
        }
        if (customOption.isSelected()) {
            isAutoThread = false;
            nThreadBox.setVisible(true);
        }
    }
}
