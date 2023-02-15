package app.controller;

import app.Manager;
import app.model.Classroom;
import app.view.ViewFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClassroomsTableController extends BaseController implements Initializable {


    ObservableList<Classroom> classrooms = FXCollections.observableArrayList();

    @FXML
    private CheckBox selectAll;
    @FXML
    private Button nextButton;
    @FXML
    TableView tableView;
    @FXML
    private TableColumn<Classroom, String> classroomColumn;
    @FXML
    private TableColumn<Classroom, Integer> capacityColumn;
    @FXML
    private TableColumn<Classroom, String> selectColumn;
    @FXML
    private TableColumn<Classroom, String> priorityColumn;

    public ClassroomsTableController(ViewFactory viewFactory, String fxmlName) {
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

        tableView.setItems(getClassrooms());
    }

    public ObservableList<Classroom> getClassrooms() {
        // this hard-coded list can be moved to NoSQL database.
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/app/csvFiles/classrooms.csv"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String room = parts[0];
                int capacity = Integer.parseInt(parts[1]);
                classrooms.add(new Classroom(room, capacity, "", ""));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classrooms;
    }

    @FXML
    void nextButtonAction() {

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
        sch.generateStudentsExamSchedule();

        Stage stage = (Stage) nextButton.getScene().getWindow();
        viewFactory.showResultWindow();
        viewFactory.closeStage(stage);
    }

    @FXML
    void prevButtonAction() {
        Stage stage = (Stage) nextButton.getScene().getWindow();
        viewFactory.showUploadWindow();
        viewFactory.closeStage(stage);
    }

}
