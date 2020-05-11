package flashget;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class to run GUI
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
        Parent root = FXMLLoader.load(getClass().getResource("/flashget/flashget.fxml"));
        primaryStage.setTitle("Multi-threaded file downloader");
        root.getStylesheets().add("/flashget/Theme.css");
        Image icon = new Image(getClass().getResourceAsStream("/image/download.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(new Scene(root, 850, 300));
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
