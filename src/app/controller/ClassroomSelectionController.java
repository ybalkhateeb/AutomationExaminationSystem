package app.controller;

import app.Manager;
import app.model.Classroom;
import app.model.DBConfig;
import app.model.Scheduler;
import app.view.ViewFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClassroomSelectionController extends BaseController implements Initializable {

    @FXML
    TableView<Classroom> tableView;
    @FXML
    private TableColumn<Classroom, String> classroomColumn;
    @FXML
    private TableColumn<Classroom, Integer> capacityColumn;
    @FXML
    private TableColumn<Classroom, String> selectColumn;
    @FXML
    private TableColumn<Classroom, String> priorityColumn;

    @FXML
    private TableColumn<Classroom, String> editColumn;

    @FXML
    private CheckBox selectAll;

    ObservableList<Classroom> classrooms;
    String query = "";
    Connection connection = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    Classroom classroom = null;

    public ClassroomSelectionController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        selectAll.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {

                ObservableList<Classroom> items = tableView.getItems();

                for (Classroom item : items) {
                    if (selectAll.isSelected())
                        item.getIsSelected().setSelected(true);
                    else
                        item.getIsSelected().setSelected(false);

                }
            }
        });
        // set the columns in the table
        classroomColumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("room"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<Classroom, Integer>("capacity"));
        selectColumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("isSelected"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<Classroom, String>("isPriority"));

        try {
            classrooms = DBConfig.getDataClassrooms();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        tableView.setItems(classrooms);

        Callback<TableColumn<Classroom, String>, TableCell<Classroom, String>> cellFoctory = (TableColumn<Classroom, String> param) -> {
            // make cell containing buttons
            final TableCell<Classroom, String> cell = new TableCell<Classroom, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);

                    } else {
                        final Button delButton = new Button("DEL");

                        delButton.setOnAction(event -> {
                            try {
                                classroom = tableView.getItems().get(getIndex());
                                query = "DELETE FROM classrooms WHERE room='"+classroom.getRoom()+"'";
                                connection = DBConfig.connectDB();
                                pst = connection.prepareStatement(query);
                                pst.execute();
                                refreshBtnAction();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });


                        HBox managebtn = new HBox(delButton);
                        managebtn.setStyle("-fx-alignment:center");
                        HBox.setMargin(delButton, new Insets(2, 2, 0, 3));

                        setGraphic(managebtn);
                        setText(null);
                    }
                }
            };
            return cell;
        };

        editColumn.setCellFactory(cellFoctory);
        tableView.setItems(classrooms);
    }

    @FXML
    void addClassroomBtnAction() {
        viewFactory.showAddClassroomView();
    }

    @FXML
    void refreshBtnAction() {
        try {
            classrooms.clear();
            classrooms = DBConfig.getDataClassrooms();
            tableView.setItems(classrooms);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void nextBtnAction() {

        List<Classroom> selectedClassrooms = new ArrayList<>();
        List<Classroom> priorityClassrooms = new ArrayList<>();

        for (Classroom classroom : classrooms) {
            if (classroom.getIsSelected().isSelected()) {
                if (classroom.getIsPriority().isSelected())
                    priorityClassrooms.add(classroom);
                selectedClassrooms.add(classroom);
            }
        }

        Manager.getInstance().setSelectedClassrooms(selectedClassrooms);
        Manager.getInstance().setPriorityClassrooms(priorityClassrooms);

        Scheduler sch = new Scheduler();
        sch.startDistributing2();

        Stage stage = (Stage) tableView.getScene().getWindow();
        viewFactory.showExamScheduleView();
        viewFactory.closeStage(stage);
    }

    @FXML
    void prevBtnAction() {
        Stage stage = (Stage) tableView.getScene().getWindow();
        viewFactory.showUploadView();
        viewFactory.closeStage(stage);
    }

}
