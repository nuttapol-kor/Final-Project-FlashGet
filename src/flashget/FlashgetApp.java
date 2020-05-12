package flashget;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class to run FlashGet program and GUI
 *
 * @author Nuttapol Korchareonrat 6210546404.
 */
public class FlashgetApp extends Application {

    /**
     * Start GUI
     *
     * @param primaryStage background stage
     * @throws Exception throws exception when found the error
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // load graphic frame
        Parent root = FXMLLoader.load(getClass().getResource("/flashget/flashget.fxml"));
        // set app title
        primaryStage.setTitle("Multi-threaded file downloader");
        // use css to styling GUI application
        root.getStylesheets().add("/flashget/Theme.css");
        // load download.png image from image folder
        Image icon = new Image(getClass().getResourceAsStream("/image/download.png"));
        // set application icon
        primaryStage.getIcons().add(icon);
        // set scene size
        primaryStage.setScene(new Scene(root, 850, 300));
        // show GUI
        primaryStage.show();
    }

    /**
     * Main to run
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
