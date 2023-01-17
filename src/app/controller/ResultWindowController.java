package app.controller;

import app.model.StudentsExamSchedule;
import app.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultWindowController extends BaseController implements Initializable {

    @FXML
    private TableColumn<StudentsExamSchedule, String> courseIDColumn;
    @FXML
    private TableColumn<StudentsExamSchedule, String> dateColumn;
    @FXML
    private TableColumn<StudentsExamSchedule, String> roomColumn;
    @FXML
    private TableColumn<StudentsExamSchedule, String> sectionColumn;
    @FXML
    private TableColumn<StudentsExamSchedule, String> sessionColumn;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<StudentsExamSchedule, String> timeColumn;
    @FXML
    private Button exportButton;

    public ResultWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       courseIDColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("course_id"));
       dateColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("date"));
       roomColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("room"));
       sessionColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("session"));
       sectionColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("section"));
       timeColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("time"));

       tableView.setItems(ExamClassroomsDistributor.getStudentsExamSchedule());

    }

    @FXML
    void exportButtonAction() {


    }

}
