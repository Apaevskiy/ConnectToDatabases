package controller;

import DAO.entity.Person;
import DAO.service.UserService;
import Models.DataBase;
import Models.MyAnchorPane;
import Models.TypeOfDataBase;
import XML.CreateXML;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class UsersController extends MyAnchorPane {

    @FXML
    private TableView<Person> tableOfDB;

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
    private TableColumn<Person, String> startWorkColumn;

    @FXML
    private Button loadToBasesButton;
    @FXML
    private Button createXMLButton;
    @FXML
    private Button connectToBaseButton;

    @FXML
    private ProgressIndicator progressIndicator;


    private DataBase dataBase;
    private UserService service;

    public UsersController() {
        super("/views/UsersPage.fxml");

        ObservableList<Person> listDataBases = FXCollections.observableArrayList();
        tableOfDB.setItems(listDataBases);

        numberColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getNumber()));
        surnameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getSurname()));
        nameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getName()));
        patronymicColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPatronymic()));
        positionColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPosition().getName()));
        departmentColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getDepartment().getName()));
        photoColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getPhoto() != null));
        keyColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue() == null ? 0 : p.getValue().getKeys().size()).asObject());
        startWorkColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getStartWork().toString()));

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
        connectToBaseButton.setOnAction(actionEvent -> {
            updateTable();
        });
        loadToBasesButton.setOnAction(actionEvent -> {

        });
        createXMLButton.setOnAction(actionEvent -> {
//            progressIndicator.getStyleClass().remove("hidden");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("src"));
            File selectedDirectory = directoryChooser.showDialog(loadToBasesButton.getScene().getWindow());
            if(selectedDirectory!=null){
                createXMLButton.setDisable(true);
                progressIndicator.setProgress(0);
                CreateXML createXML = new CreateXML(tableOfDB.getItems(),
                        service.getAllDepartments(),
                        service.getAllPositions(),
                        selectedDirectory.getAbsolutePath());
                progressIndicator.progressProperty().unbind();
                progressIndicator.progressProperty().bind(createXML.progressProperty());
                createXML.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                        t -> {
                            try {
                                Runtime.getRuntime().exec("explorer.exe /select," + selectedDirectory.getAbsolutePath()+"\\data.xml");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            createXMLButton.setDisable(false);
                        });
                createXML.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED,
                        t -> createXMLButton.setDisable(false));
                new Thread(createXML).start();
            }
//            progressIndicator.getStyleClass().add("hidden");
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
                        dataBase = new DataBase(path, user, password, comment, type, null);
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

    private List<Person> connectToMainDatabase(DataBase base) {
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
