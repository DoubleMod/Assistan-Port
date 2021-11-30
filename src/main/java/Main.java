import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            URL resource = getClass().getResource("SerialAssistant.fxml");
            root = FXMLLoader.load(resource);
            primaryStage.setTitle("串口调试");
//            primaryStage.getIcons().add(new Image("avator2.png"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
