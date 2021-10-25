package controller;

import DAO.entity.Person;
import Models.MyAnchorPane;
import XML.LoadXML;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class XMLController extends MyAnchorPane {
    @FXML
    private TableView<Person> tableOfDB;

    @FXML
    private TableColumn<Person, Long> idColumn;
    @FXML
    private TableColumn<Person, String> numberColumn;
    @FXML
    private TableColumn<Person, String> surnameColumn;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> patronymicColumn;
    @FXML
    private TableColumn<Person, String> positionColumn;
    @FXML
    private TableColumn<Person, String> departmentColumn;
    @FXML
    private TableColumn<Person, Boolean> photoColumn;
    @FXML
    private TableColumn<Person, Integer> keyColumn;
    @FXML
    private TableColumn<Person, LocalDate> startWorkColumn;

    @FXML
    private Button loadXMLButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button loadToBasesButton;

    private Map<Long, String> departments = new HashMap<>();
    private Map<Long, String> positions = new HashMap<>();

    public XMLController() {
        super("/views/XMLPage.fxml");

        ObservableList<Person> listDataBases = FXCollections.observableArrayList();
        tableOfDB.setItems(listDataBases);

        idColumn.setCellValueFactory(p -> new SimpleLongProperty(p.getValue().getId()).asObject());
        numberColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getNumber()));
        surnameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getSurname()));
        nameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getName()));
        patronymicColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPatronymic()));
        positionColumn.setCellValueFactory(p -> new SimpleStringProperty(positions.get(p.getValue().getPosition().getId())));
        departmentColumn.setCellValueFactory(p -> new SimpleStringProperty(departments.get(p.getValue().getDepartment().getId())));
        photoColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getPhoto() != null));
        keyColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getKeys() == null ? 0 : p.getValue().getKeys().size()).asObject());
        startWorkColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStartWork()));

        photoColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean check, boolean empty) {
                super.updateItem(check, empty);
                if (check == null) {
                    setStyle("");
                    setText(null);
                } else if (check) {
                    setText(new String("\u2713".getBytes(), StandardCharsets.UTF_8));
                    setStyle("-fx-text-fill: limegreen; -fx-font-size: 16");
                } else {
                    setText(new String("\u29BB".getBytes(), StandardCharsets.UTF_8));
                    setStyle("-fx-text-fill: red; -fx-font-size: 16");
                }

            }
        });
        keyColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer check, boolean empty) {
                super.updateItem(check, empty);
                if (check == null || check == 0) {
                    setText(new String("\u29BB".getBytes(), StandardCharsets.UTF_8));
                    setStyle("-fx-text-fill: red; -fx-font-size: 16");
                } else if (check > 0) {
                    setText(new String("\u2713".getBytes(), StandardCharsets.UTF_8));
                    setStyle("-fx-text-fill: limegreen; -fx-font-size: 16");
                }
            }
        });
        loadXMLButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("src"));
            File selectedDirectory = directoryChooser.showDialog(loadToBasesButton.getScene().getWindow());
            if (selectedDirectory != null) {
                progressIndicator.getStyleClass().removeAll("hidden");
                loadXMLButton.setDisable(true);
                progressIndicator.setProgress(0);
                LoadXML loadXML = new LoadXML(selectedDirectory.getAbsolutePath());
                progressIndicator.progressProperty().unbind();
                progressIndicator.progressProperty().bind(loadXML.progressProperty());
                loadXML.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                        t -> {
                            tableOfDB.setItems(FXCollections.observableArrayList(loadXML.getPeople()));
                            loadXMLButton.setDisable(false);
                            departments = loadXML.getDepartments();
                            positions = loadXML.getPositions();
                                    progressIndicator.getStyleClass().add("hidden");
                        });
                loadXML.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED,
                        t -> {
                            loadXMLButton.setDisable(false);
                            tableOfDB.setItems(FXCollections.observableArrayList());
                            progressIndicator.getStyleClass().add("hidden");
                        });
                new Thread(loadXML).start();
            }
        });
        loadToBasesButton.setOnAction(actionEvent -> {
        });
    }
}
