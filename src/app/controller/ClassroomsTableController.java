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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// get the selected and priority classrooms
public class ClassroomsTableController extends BaseController implements Initializable {

    public ClassroomsTableController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

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
    @FXML
    private CheckBox selectAll;
    @FXML
    private Button nextButton;
    ObservableList<Classroom> classrooms;
    List<Classroom> selectedClassRooms = new ArrayList<>();
    List<Classroom> priorityClassroom = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle rb) { selectAll.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {

                for (Classroom item : selectedClassRooms) {
                    if (selectAll.isSelected())
                        item.getIsSelected().setSelected(true);
                    else
                        item.getIsPriority().setSelected(false);

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
        classrooms = FXCollections.observableArrayList(
                new Classroom("F158", 30, "", ""),
                new Classroom("F175", 25, "", ""),
                new Classroom("F178", 25, "", ""),
                new Classroom("F180", 20, "", ""),
                new Classroom("F181", 30, "", ""),
                new Classroom("F182", 35, "", ""),
                new Classroom("F183", 35, "", ""),
                new Classroom("F184", 25, "", ""),
                new Classroom("F185", 25, "", ""),
                new Classroom("F186", 30, "", ""),
                new Classroom("F187", 25, "", ""),
                new Classroom("F191", 30, "", ""),
                new Classroom("S289", 25, "", ""),
                new Classroom("S291", 35, "", ""),
                new Classroom("S292", 30, "", ""),
                new Classroom("S293", 30, "", ""),
                new Classroom("S301", 40, "", ""),
                new Classroom("S302", 25, "", ""),
                new Classroom("S303", 30, "", ""),
                new Classroom("S304", 35, "", ""),
                new Classroom("CSSE Library", 100, "", ""),
                new Classroom("College Of Science", 100, "", "")
        );

        return classrooms;
    }

    @FXML
    void nextButtonAction() {
        for (Classroom bean : classrooms) {
            if (bean.getIsSelected().isSelected()) {
                if (bean.getIsPriority().isSelected())
                    priorityClassroom.add(bean);
                selectedClassRooms.add(bean);
            }
        }

        Manager obj = Manager.getInstance();
        obj.setSelectedClassrooms(selectedClassRooms);
        obj.setPriorityClassrooms(priorityClassroom);

        ExamClassroomsDistributor tmp = new ExamClassroomsDistributor();
        tmp.start();

        Stage stage = (Stage) nextButton.getScene().getWindow();
        viewFactory.showResultWindow();
        viewFactory.closeStage(stage);
    }

}
