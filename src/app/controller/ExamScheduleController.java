package app.controller;

import app.model.ExamSchedule;
import app.model.PDFExporter;
import app.model.Scheduler;
import app.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

public class ExamScheduleController extends BaseController implements Initializable {

    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<ExamSchedule, String> courseIDColumn;
    @FXML
    private TableColumn<ExamSchedule, String> dateColumn;
    @FXML
    private TableColumn<ExamSchedule, String> roomColumn;
    @FXML
    private TableColumn<ExamSchedule, String> proctorColumn;
    @FXML
    private TableColumn<ExamSchedule, String> sectionColumn;
    @FXML
    private TableColumn<ExamSchedule, String> sessionColumn;
    @FXML
    private TableColumn<ExamSchedule, String> timeColumn;

    public ExamScheduleController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       courseIDColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("course_id"));
       dateColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("date"));
       roomColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("rooms"));
       proctorColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("proctors"));
       sessionColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("session"));
       sectionColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("section"));
       timeColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("time"));

       tableView.setItems(Scheduler.getStudentsExamSchedule());
    }

    @FXML
    void prevBtnAction() {
        tableView.getItems().clear();
        Stage stage = (Stage) tableView.getScene().getWindow();
        viewFactory.showClassroomSelectionView();
        viewFactory.closeStage(stage);
    }

    @FXML
    void exportBtnAction() throws IOException, ParseException {
        String home = System.getProperty("user.home");
        String desktop = "Desktop";

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            desktop = "Desktop";
        } else if (os.contains("mac")) {
            desktop = "Desktop";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            desktop = "Desktop";
        } else {
            // Unsupported OS
            System.err.println("Unsupported OS detected: " + os);
        }

        File studentsFile = new File(home, desktop + File.separator + "CCSE Male Final Exam Schedule Students.pdf");
        File proctorsFile = new File(home, desktop + File.separator + "CCSE Male Final Exam Schedule Proctors.pdf");
        File reportFile = new File(home, desktop + File.separator + "Proctors Report.pdf");

        PDFExporter exporter = new PDFExporter(tableView.getItems());
        exporter.createFileForStudents(studentsFile);
        exporter.createFileForProctors(proctorsFile);
        exporter.createReportFile(reportFile);
    }

    @FXML
    void exportBtnForProctorsAction() throws IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            PDFExporter exporter = new PDFExporter(tableView.getItems());
            exporter.createReportFile(new File(file.getAbsoluteFile() + ".pdf"));

        }
    }

}

