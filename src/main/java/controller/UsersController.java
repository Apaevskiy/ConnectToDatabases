package controller;

import DAO.entity.Key;
import DAO.entity.Person;
import DAO.service.DatabaseService;
import DAO.service.UserService;
import Models.Database;
import Models.MyAnchorPane;
import Models.TypeOfDataBase;
import XML.CreateXML;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

public class UsersController extends MyAnchorPane {

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
    private Button loadToBasesButton;
    @FXML
    private Button createXMLButton;
    @FXML
    private Button connectToBaseButton;

    @FXML
    private ProgressIndicator progressIndicator;


    private Database dataBase;
    private UserService service;

    public UsersController() {
        super("/views/UsersPage.fxml");

        ObservableList<Person> listDataBases = FXCollections.observableArrayList();
        tableOfDB.setItems(listDataBases);
        tableOfDB.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        idColumn.setCellValueFactory(p -> new SimpleLongProperty(p.getValue().getId()).asObject());
        numberColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getNumber()));
        surnameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getLastName()));
        nameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getFirstName()));
        patronymicColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getMiddleName()));
        positionColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPosition().getName()));
        departmentColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getDepartment().getName()));
        photoColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getPhoto() != null));
        keyColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getKeys() == null ? 0 : p.getValue().getKeys().size()).asObject());
        startWorkColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStartWork()));

        startWorkColumn.setCellFactory(p -> new TableCell<>() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                }
                setGraphic(null);
            }
        });
        startWorkColumn.setComparator(Comparator.comparingLong(o -> o == null ? 0 : o.toEpochDay()));
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

        ContextMenu cm = new ContextMenu();
        MenuItem pickAllMenu = new MenuItem("Выделить всё");
        pickAllMenu.setOnAction(e -> tableOfDB.getSelectionModel().selectAll());
        MenuItem addKeyMenu = new MenuItem("Пропуска");
        addKeyMenu.setOnAction(e -> {
            SelectionModel<Person> model = tableOfDB.getSelectionModel();
            if (model != null && model.getSelectedItem() != null) {
                Person person = model.getSelectedItem();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Keys.fxml"));
                Scene newScene;
                try {
                    newScene = new Scene(loader.load());
                    loader.<KeyController>getController().setKeys(person);
                    Stage inputStage = new Stage();
                    inputStage.initOwner(tableOfDB.getScene().getWindow());
                    inputStage.setScene(newScene);
                    inputStage.showAndWait();
                    List<Key> newKeys = loader.<KeyController>getController().getKeys();
                    if (service.updateKeys(newKeys, person.getId())) {
                        person.setKeys(service.getKeysByPerson(person));
                        tableOfDB.refresh();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        cm.getItems().addAll(pickAllMenu, addKeyMenu);
        tableOfDB.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                tableOfDB.setContextMenu(cm);
            }
        });

        connectToBaseButton.setOnAction(actionEvent -> {
            updateTable();
        });
        loadToBasesButton.setOnAction(actionEvent -> {
            TableView.TableViewSelectionModel<Person> model = tableOfDB.getSelectionModel();
            List<Person> people = model == null ? tableOfDB.getItems() : model.getSelectedItems();
            if (people != null) {
                progressIndicator.getStyleClass().removeAll("hidden");
                loadToBasesButton.setDisable(true);
                progressIndicator.setProgress(0);
                DatabaseService databaseService = new DatabaseService(
                        people, service.getAllDepartments(), service.getAllPositions()
                );
                progressIndicator.progressProperty().unbind();
                progressIndicator.progressProperty().bind(databaseService.progressProperty());
                databaseService.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t -> {
                    progressIndicator.getStyleClass().add("hidden");
                    createXMLButton.setDisable(false);
                });
                databaseService.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, t -> {
                    createXMLButton.setDisable(false);
                    progressIndicator.getStyleClass().add("hidden");
                });
                new Thread(databaseService).start();
            }
        });
        createXMLButton.setOnAction(actionEvent -> {
            TableView.TableViewSelectionModel<Person> model = tableOfDB.getSelectionModel();
            List<Person> people = model == null ? tableOfDB.getItems() : model.getSelectedItems();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("src"));
            File selectedDirectory = directoryChooser.showDialog(loadToBasesButton.getScene().getWindow());
            if (selectedDirectory != null) {
                progressIndicator.getStyleClass().removeAll("hidden");
                createXMLButton.setDisable(true);
                progressIndicator.setProgress(0);
                CreateXML createXML = new CreateXML(people,
                        service.getAllDepartments(),
                        service.getAllPositions(),
                        selectedDirectory.getAbsolutePath());
                progressIndicator.progressProperty().unbind();
                progressIndicator.progressProperty().bind(createXML.progressProperty());
                createXML.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, t -> {
                    try {
                        Runtime.getRuntime().exec("explorer.exe /select," + selectedDirectory.getAbsolutePath() + "\\data.xml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressIndicator.getStyleClass().add("hidden");
                    createXMLButton.setDisable(false);
                });
                createXML.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, t -> {
                    createXMLButton.setDisable(false);
                    progressIndicator.getStyleClass().add("hidden");
                });
                new Thread(createXML).start();
            }
        });
    }

    public void updateTable() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            DefaultHandler defaultHandler = new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equals("database")
                            && TypeOfDataBase.valueOf(
                            attributes.getValue("type")).equals(TypeOfDataBase.AlfaBase)) {
                        String path = attributes.getValue("path");
                        String user = attributes.getValue("user");
                        String password = attributes.getValue("password");
                        TypeOfDataBase type = TypeOfDataBase.valueOf(attributes.getValue("type"));
                        String comment = attributes.getValue("comment");
                        dataBase = new Database(path, user, password, comment, type);
                    }
                }
            };
            parser.parse(new File("src/main/resources/databases.xml"), defaultHandler);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        List<Person> list = connectToMainDatabase(dataBase);
        if (list == null) {
            tableOfDB.getItems().clear();
        } else {
            tableOfDB.setItems(FXCollections.observableList(list));
        }
    }

    private List<Person> connectToMainDatabase(Database base) {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            try {
                Connection connection = DriverManager.getConnection(
                        "jdbc:firebirdsql://" + base.getPath() + "?sql_dialect=3&lc_ctype=WIN1251",
                        base.getUser(),
                        base.getPassword());
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    service = new UserService(statement);
                    return service.getAllUsers();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
