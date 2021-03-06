package controller;

import Models.MyAnchorPane;
import XML.XMLHandler;
import Models.Database;
import Models.TypeOfDataBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

public class SettingController extends MyAnchorPane {

    @FXML
    private Button checkButton;

    @FXML
    private TableColumn<Database, String> commentColumn;
    @FXML
    private TableColumn<Database, String> passwordColumn;
    @FXML
    private TableColumn<Database, String> pathColumn;
    @FXML
    private TableColumn<Database, Boolean> checkColumn;
    @FXML
    private TableColumn<Database, TypeOfDataBase> typeColumn;
    @FXML
    private TableColumn<Database, String> userColumn;

    @FXML
    private TableView<Database> tableOfDB;


    public SettingController() {
        super("/views/SettingPage.fxml");
        ObservableList<Database> listDatabases = FXCollections.observableArrayList();
        tableOfDB.setItems(listDatabases);
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLHandler handler = new XMLHandler();
            parser.parse(new File("src/main/resources/databases.xml"), handler);
            tableOfDB.getItems().addAll(handler.getList());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }


        commentColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getComment()));
        passwordColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPassword()));
        pathColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPath()));
        checkColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getCheckConnect()));
        typeColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getType()));
        userColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getUser()));

        commentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        pathColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(TypeOfDataBase.values()));

        checkColumn.setCellFactory(column -> new TableCell<>() {
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

        commentColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Database, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                                .setComment(t.getNewValue())
        );
        passwordColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Database, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                                .setPassword(t.getNewValue())
        );
        pathColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Database, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                                .setPath(t.getNewValue())
        );
        typeColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Database, TypeOfDataBase> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                                .setType(t.getNewValue())
        );
        userColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Database, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                                .setUser(t.getNewValue())
        );

        ContextMenu cm = new ContextMenu();
        MenuItem menuAdd = new MenuItem("???????????????? ???????? ????????????");
        menuAdd.setOnAction(e -> {
            tableOfDB.getItems().add(new Database());
            tableOfDB.getSelectionModel().selectLast();
        });
        MenuItem menuDelete = new MenuItem("?????????????? ???????? ????????????");
        menuDelete.setOnAction(e -> {
            TableView.TableViewSelectionModel<Database> model = tableOfDB.getSelectionModel();
            if (model != null && model.getSelectedCells().size() != 0)
                tableOfDB.getItems().remove(tableOfDB.getSelectionModel().getSelectedItem());
        });
        cm.getItems().addAll(menuAdd, menuDelete);

        tableOfDB.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                tableOfDB.setContextMenu(cm);
            }
        });

        checkButton.setOnAction(actionEvent -> {
            checkButton.setDisable(true);
            AtomicInteger i = new AtomicInteger();
            for (Database db : tableOfDB.getItems()) {
                Runnable r = () -> {
                    db.setCheckConnect(null);
                    if (!db.equals(new Database())) {
                        testingDatabase(db);
                        tableOfDB.refresh();
                    }
                    i.getAndIncrement();
                    if (i.get() == tableOfDB.getItems().size()) {
                        checkButton.setDisable(false);
                    }
                };
                Thread myThread = new Thread(r);
                myThread.start();
            }
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();
                Element rootElement =
                        doc.createElementNS("https://github.com/Apaevskiy/", "databases");
                doc.appendChild(rootElement);
                for (Database db : tableOfDB.getItems()) {
                    if (!db.equals(new Database())) {
                        Element database = doc.createElement("database");
                        database.setAttribute("path", db.getPath());
                        database.setAttribute("user", db.getUser());
                        database.setAttribute("password", db.getPassword());
                        database.setAttribute("type", db.getType() == null ? null : db.getType().toString());
                        database.setAttribute("comment", db.getComment());
                        rootElement.appendChild(database);
                    }
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult file = new StreamResult(new File("src/main/resources/databases.xml"));
                transformer.transform(source, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void testingDatabase(Database dataBase) {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            dataBase.setCheckConnect(false);
            return;
        }
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:firebirdsql://" + dataBase.getPath() + "?sql_dialect=3&lc_ctype=WIN1251",
                    dataBase.getUser(),
                    dataBase.getPassword());
            if (connection != null) {
                connection.createStatement();
                dataBase.setCheckConnect(true);
            } else {
                dataBase.setCheckConnect(false);
            }

        } catch (SQLException ignored) {
            dataBase.setCheckConnect(false);
        }
    }
}
