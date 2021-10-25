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
    public Button xmlButton;

    @FXML
    void initialize() {
        SettingController settingController = new SettingController();
        UsersController usersController = new UsersController();
        XMLController xmlController = new XMLController();
        workPlace.getChildren().addAll(settingController, usersController, xmlController);

        settingDBButton.setOnAction(event -> {
            hiddenPages(workPlace.getChildren(), settingController);
        });
        userButton.setOnAction(event -> {
            hiddenPages(workPlace.getChildren(), usersController);
        });
        xmlButton.setOnAction(event -> {
            hiddenPages(workPlace.getChildren(), xmlController);
        });
    }

    private void hiddenPages(List<Node> list, Node pane) {
        for (Node node : list) {
            if (node.equals(pane)) {
                node.getStyleClass().removeAll("hidden");
            } else {
                if (!node.getStyleClass().contains("hidden"))
                    node.getStyleClass().add("hidden");
            }
        }
    }
}
