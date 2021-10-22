package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MainController {
    public AnchorPane workPlace;
    public Button settingDBButton;
    public Button userButton;

    @FXML
    void initialize() {
        SettingController settingController = new SettingController();
        UsersController usersController = new UsersController();
        workPlace.getChildren().addAll(settingController, usersController);

//        List<Node> buttonList = Arrays.asList(settingDBButton, userButton);
        settingDBButton.setOnAction(event -> {
            settingController.switchPane(SettingController.class);
//            clearSelected(buttonList, addUserButton);
        });
        userButton.setOnAction(event -> {
            usersController.switchPane(UsersController.class);
//            clearSelected(buttonList, addUserButton);
        });
    }
}
