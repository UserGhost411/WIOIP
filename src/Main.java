import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/View/Main.fxml"));
        primaryStage.setTitle("Weather Information Over Internet Protocol");
        Scene sc = new Scene(root);
        sc.getStylesheets().add(getClass().getResource("/res/main.css").toExternalForm());
        primaryStage.setScene(sc);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
