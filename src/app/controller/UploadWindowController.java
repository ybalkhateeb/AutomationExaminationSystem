package app.controller;

import app.Manager;
import app.view.ViewFactory;
import app.controller.ExamClassroomsDistributor;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class UploadWindowController extends BaseController{

    @FXML
    private Button browseCollegeButton;

    @FXML
    private Button browseExamButton;

    @FXML
    private Label collegeBrowseLabel;

    @FXML
    private Label examBrowseLabel;

    @FXML
    private Button nextButton;

    File examFile;

    File collegeFile;

    public UploadWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @FXML
    void browseExamButtonAction() {
        Stage stage = (Stage) collegeBrowseLabel.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        examFile = fileChooser.showOpenDialog(stage);
        examBrowseLabel.setText(examFile.getName());

        if (collegeFile != null)
            nextButton.setDisable(false);
    }

    @FXML
    void collegeBrowseButtonAction() {
        Stage stage = (Stage) browseExamButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        collegeFile = fileChooser.showOpenDialog(stage);
        collegeBrowseLabel.setText(collegeFile.getName());

        if (examFile != null)
            nextButton.setDisable(false);
    }

    @FXML
    void nextButtonAction() {
        Manager obj = Manager.getInstance();
        obj.setExamFile(examFile);
        obj.setCollegeFile(collegeFile);

        viewFactory.showClassroomsTableWindow();
        Stage stage = (Stage) nextButton.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

}
