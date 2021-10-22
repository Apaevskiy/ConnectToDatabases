package Config;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {

        Application.launch();
    }

    public void start(Stage stage) throws Exception {
        stage.setTitle("Вы все говно!");
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/views/Menu.fxml");
        stage.setWidth(1000);
        stage.setHeight(600);
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        stage.setScene(new Scene(root));

        stage.show();
    }
}