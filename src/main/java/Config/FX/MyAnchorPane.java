package Config.FX;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.util.*;

public abstract class MyAnchorPane extends AnchorPane {
    private static final Map<String, Anchor> anchorPaneList = new HashMap<>();
    private TimerForHidingNodes timer;

    public MyAnchorPane(String url, PriorityType type) {
        timer();
        this.setId(this.getTypeSelector());
        anchorPaneList.put(this.getTypeSelector(), new Anchor(this, type));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.getStyleClass().add("hidden");
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        this.setPrefSize(200.0, 200.0);
    }

    public abstract void timer();

    public void setTimer(TimerForHidingNodes timer) {
        this.timer = timer;
    }

    @Data
    @Getter
    @AllArgsConstructor
    private static class Anchor {
        private MyAnchorPane anchorPane;
        private PriorityType type;
    }

    public enum PriorityType {
        PRIORITY_PAGE,
        NON_PRIORITY_PAGE
    }

//    @SuppressWarnings("unchecked")
    public /*<T extends MyAnchorPane>  T*/ void switchPane(Class<?/*T*/> myClass) {
        String id = myClass.getSimpleName();
//        T a = null;
        Anchor type = anchorPaneList.get(myClass.getSimpleName());
        for (Anchor pane : anchorPaneList.values()) {
            if (type.getType() == pane.getType()) {
                ObservableList<String> style = pane.getAnchorPane().getStyleClass();
                if (pane.getAnchorPane().getId().equals(id)) {
                    style.remove("hidden");
//                    a = (T) pane.getAnchorPane();
                } else {
                    if (!style.contains("hidden")) {
                        style.add("hidden");
                    }
                }
            }
        }
//        return a;
    }

    protected int getIntData(TextInputControl textField) {
        try {
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected double getDoubleData(TextInputControl textField) {
        try {
            return Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected String getStringData(TextInputControl textField) {
        String result = textField.getText();
        if (result != null && !result.replaceAll(" ", "").equals("")) {
            return result;
        }
        return null;
    }

    protected void sendMessage(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.getStyleClass().removeAll("hidden");
            label.getStyleClass().add("message");
        }
        timer.schedule();
    }

    protected void error(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.getStyleClass().removeAll("message");
            label.getStyleClass().removeAll("hidden");
            timer.schedule();
        }
    }

    protected void error(Node field, Label label) {
        if (field != null) field.getStyleClass().add("error");
        if (label != null) {
            label.getStyleClass().removeAll("hidden");
            label.getStyleClass().removeAll("message");
        }
        timer.schedule();
    }

    protected void error(Node field, Label label, String message) {
        if (field != null) {
            field.getStyleClass().add("error");
        }
        if (label != null) {
            label.setText(message);
            label.getStyleClass().removeAll("message");
            label.getStyleClass().removeAll("hidden");
        }
        timer.schedule();
    }

    protected void clearSelected(List<Node> nodes, Node selectNode) {
        String style = "select";
        for (Node node : nodes) {
            if (node != selectNode)
                node.getStyleClass().removeAll(style);
            else if (!node.getStyleClass().contains(style))
                node.getStyleClass().add(style);
        }
    }

    protected javafx.event.EventHandler<? super javafx.scene.input.KeyEvent> getEvent(Node to, Button button) {
        return keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                button.fire();
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.TAB) {
                to.requestFocus();
                keyEvent.consume();
            }
        };
    }
}
