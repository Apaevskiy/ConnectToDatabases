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

    @Override
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
        /*FlowPane root = new FlowPane();
        root.setPadding(new Insets(20));
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            try {
                Connection connection = DriverManager.getConnection(
                        "jdbc:firebirdsql://localhost/base?sql_dialect=3&lc_ctype=WIN1251",
                        "SYSDBA",
                        "1");
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    try {
                        ResultSet resultSet = statement.executeQuery("select PICT from SP_OL_BLOBS");
                        if(resultSet.next()){
                            Blob blob = resultSet.getBlob("PICT");
                            byte[] bytes = blob == null ? null : blob.getBytes(1, (int) blob.length());
                            if (blob!=null){
                                InputStream in = blob.getBinaryStream();
                                Image image = new Image(in);
                                ImageView imageView = new ImageView(image);
                                root.getChildren().addAll(imageView);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 400, 200);
        stage.setTitle("JavaFX ImageView (o7planning.org)");
        stage.setScene(scene);
        stage.show();*/
    }
}