package app.controller;

import app.Manager;
import app.view.ViewFactory;
import com.opencsv.CSVReader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class UploadWindowController2 extends BaseController{

    @FXML
    private ImageView checkmark1;

    @FXML
    private ImageView checkmark2;

    @FXML
    private ImageView checkmark3;

    @FXML
    private Button collegeFileBtn;

    @FXML
    private Button deleteBtn1;

    @FXML
    private Button deleteBtn2;

    @FXML
    private Button deleteBtn3;

    @FXML
    private ImageView examDeleteButton;

    @FXML
    private Button examFileBtn;

    @FXML
    private Region fileRegion;

    @FXML
    private Region fileRegion2;

    @FXML
    private Region fileRegion3;

    @FXML
    private Button nextBtn;

    @FXML
    private Button proctorsFileBtn;

    @FXML
    private Label collegeLabel;

    @FXML
    private Label examLabel;

    @FXML
    private Label proctorsLabel;
    @FXML
    private Label errorLabel;

    File examFile;
    File collegeFile;
    File proctorsFile;

    public UploadWindowController2(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @FXML
    void examFileBtnAction () {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        examFile = fc.showOpenDialog(null);
        fileAdded(examFile, checkmark1, deleteBtn1, examLabel, examFileBtn);
    }

    @FXML
    void collegeFileBtnAction() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        collegeFile = fc.showOpenDialog(null);
        fileAdded(collegeFile, checkmark2, deleteBtn2, collegeLabel, collegeFileBtn);
    }

    @FXML
    void proctorsFileBtnAction() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        proctorsFile = fc.showOpenDialog(null);
        fileAdded(proctorsFile, checkmark3, deleteBtn3, proctorsLabel, proctorsFileBtn);
    }

    private void fileAdded(File file, ImageView checkmark, Button deleteBtn, Label fileLabel, Button fileBtn) {

        if (file != null)  {
            fileBtn.setVisible(false);
            fileLabel.setText(file.getName());
            fileLabel.setVisible(true);
            deleteBtn.setVisible(true);
            checkmark.setVisible(true);
        }
    }

    @FXML
    void deleteBtnAction1() {
       examFile = null;
       deleteAction(checkmark1, deleteBtn1, examLabel, examFileBtn);
    }

    @FXML
    void deleteBtnAction2() {
        collegeFile = null;
        deleteAction(checkmark2, deleteBtn2, collegeLabel, collegeFileBtn);
    }

    @FXML
    void deleteBtnAction3() {
        proctorsFile = null;
        deleteAction(checkmark3, deleteBtn3, proctorsLabel, proctorsFileBtn);
    }

    private void deleteAction(ImageView checkmark, Button deleteBtn, Label fileLabel, Button btn) {
        fileLabel.setVisible(false);
        checkmark.setVisible(false);
        deleteBtn.setVisible(false);

        btn.setVisible(true);
    }

    @FXML
    void nextBtnAction() {
        if (examFile != null && collegeFile != null && proctorsFile != null) {
            if (checkFilesContent()) {
                Manager.getInstance().setExamFile(examFile);
                Manager.getInstance().setCollegeFile(collegeFile);
                Manager.getInstance().setProctorsFile(proctorsFile);

                Stage stage = (Stage) nextBtn.getScene().getWindow();
                ViewFactory viewFactory = new ViewFactory();
                viewFactory.showClassroomsTableWindow();
                viewFactory.closeStage(stage);
            }
            else {
                errorLabel.setText("Please check the files content");
            }
        }
        else {
            errorLabel.setText("Please upload all the required files");
        }
    }

    boolean checkFilesContent() {
        try (CSVReader reader = new CSVReader(new FileReader(examFile))) {
            String[] values = reader.readNext();
            if (!(values[0].equals("course_id") && values[1].equals("session") && values[2].equals("date")))
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (CSVReader reader = new CSVReader(new FileReader(collegeFile))) {
            String[] values = reader.readNext();
            if (!(values[0].equals("SEMESTER_TERM_CODE") && values[1].equals("SSBSECT_CAMPS_CODE")
                    && values[2].equals("CRS_TITLE") && values[3].equals("INSTRUCTOR_ID")
                    && values[4].equals("INSTRUCTOR_NAME") && values[5].equals("CRSE_SUBJCT")
                    && values[6].equals("CRSE_NUM") && values[7].equals("CRN")
                    && values[8].equals("ST_ID") && values[9].equals("ST_NAME")))
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (CSVReader reader = new CSVReader(new FileReader(proctorsFile))) {
            String[] values = reader.readNext();
            if (!(values[0].equals("INSTRUCTOR_TYPE") && values[1].equals("INSTRUCTOR_ID") && values[2].equals("INSTRUCTOR_NAME")
                && values[3].equals("TEACHING_HOURS") && values[4].equals("OFFICE_WORK")))
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
