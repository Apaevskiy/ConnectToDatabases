package controller;

import DAO.entity.Key;
import DAO.entity.Person;
import Models.DateEditingCell;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

public class KeyController {
    @FXML
    private TableView<Key> table;

    @FXML
    private TableColumn<Key, Long> idColumn;
    @FXML
    private TableColumn<Key, String> numberColumn;
    @FXML
    private TableColumn<Key, LocalDate> dateStart;
    @FXML
    private TableColumn<Key, LocalDate> dateFinish;

    @FXML
    private Label personField;

    public List<Key> getKeys(){
        return table.getItems();
    }
    public void setKeys(Person p){
        table.setItems(FXCollections.observableArrayList(p.getKeys()));
        personField.setText(p.getNumber() + " " + p.getSurname() + " " + p.getName() + " " + p.getPatronymic());
    }
    @FXML
    void initialize() {

        idColumn.setCellValueFactory(p -> new SimpleLongProperty(p.getValue().getId()).asObject());
        numberColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getNumber()));
        dateStart.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStart()));
        dateFinish.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getFinish()));

        dateStart.setCellFactory(p -> new DateEditingCell<>());
        dateFinish.setCellFactory(p -> new TableCell<>() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                }
                setGraphic(null);
            }
        });
        dateStart.setComparator(Comparator.comparingLong(o -> o == null ? 0 : o.toEpochDay()));
        dateFinish.setComparator(Comparator.comparingLong(o -> o == null ? 0 : o.toEpochDay()));

        numberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        numberColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Key, String> t) ->
                        (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                                .setNumber(t.getNewValue()));

        ContextMenu cm = new ContextMenu();
        MenuItem closeKeyMenu = new MenuItem("Закрыть пропуск");
        closeKeyMenu.setOnAction(e -> {
            SelectionModel<Key> model = table.getSelectionModel();
            if(model != null && model.getSelectedItem()!=null){
                Key key = model.getSelectedItem();
                key.setFinish(LocalDate.now());
                table.refresh();
            }
        });
        MenuItem setKeyMenu = new MenuItem("Добавить пропуск");
        setKeyMenu.setOnAction(e -> {
            table.getItems().add(new Key());
            table.getSelectionModel().selectLast();
        });
        cm.getItems().addAll(setKeyMenu, closeKeyMenu);
        table.addEventHandler(MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                table.setContextMenu(cm);
            }
        });
    }
}
