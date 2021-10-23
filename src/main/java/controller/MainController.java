package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class MainController {
    public AnchorPane workPlace;
    public Button settingDBButton;
    public Button userButton;

    @FXML
    void initialize() {
        SettingController settingController = new SettingController();
        UsersController usersController = new UsersController();
        workPlace.getChildren().addAll(settingController, usersController);

        settingDBButton.setOnAction(event -> {
            hiddenPages(workPlace.getChildren(), settingController);
        });
        userButton.setOnAction(event -> {
            hiddenPages(workPlace.getChildren(), usersController);
        });
    }
    private void hiddenPages(List<Node> list, AnchorPane pane) {
        for (Node node : list) {
            if (node != pane)
                node.getStyleClass().add("hidden");
            else node.getStyleClass().remove("hidden");
        }
    }
}
