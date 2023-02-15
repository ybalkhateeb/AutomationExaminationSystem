package app.controller;

import app.Manager;
import app.view.ViewFactory;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadWindowController extends BaseController{

    @FXML
    private Region fileRegion;
    @FXML
    private Label errorLabel;
    @FXML
    private Button nextButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button browseLinkButton;
    @FXML
    private ListView filesListView;
    private List<File> files = new ArrayList<>();

    public UploadWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }


    @FXML
    void browseFilesButtonAction() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv", ".CSV"));
        List<File> selectedFile =  fileChooser.showOpenMultipleDialog(null);

        if (selectedFile != null) {
            for (File file : selectedFile) {
                if (!files.contains(file)) {
                    files.add(file);
                    filesListView.getItems().add(file.getName());
                }
            }
            filesListView.setVisible(true);
            nextButton.setDisable(false);
        }
    }

    @FXML
    void regionOnDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            List<File> selectedFiles = new ArrayList<>();
            for (int i = 0; i < db.getFiles().size(); i++) {
                if (db.getFiles().get(i).getName().endsWith(".csv")) {
                    // process CSV file
                    selectedFiles.add(db.getFiles().get(i));
                } else {
                    errorLabel.setText("File: " + db.getFiles().get(i).getName() + " is not a CSV file.");
                }
            }
            addFiles(selectedFiles);
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void regionOnDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles())
        {
            // allow for both copying and moving, whatever user chooses
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void addFiles(List<File> selectedFiles) {

        for (File file : selectedFiles) {
            if (!files.contains(file)) {
                files.add(file);
                filesListView.getItems().add(file.getName());
            }
        }

        filesListView.setVisible(true);
        clearButton.setVisible(true);
        nextButton.setDisable(false);
    }

    @FXML
    void clearButtonAction() {
        filesListView.getItems().clear();
        files.clear();
        nextButton.setDisable(true);
        filesListView.setVisible(false);
    }

    @FXML
    void nextButtonAction() {
        File examFile = null;
        File collegeFile = null;
        File proctorsFile = null;

        for (File file : files) {
            if (file.getName().toLowerCase().contains("exams")) {
                examFile = file;
            }
            else if (file.getName().toLowerCase().contains("college") || file.getName().toLowerCase().contains("csse")){
                collegeFile = file;
            }
            else if (file.getName().toLowerCase().contains("proctors")){
                proctorsFile = file;
            }
        }


        if (examFile != null && collegeFile != null && proctorsFile != null) {
            Manager mng = Manager.getInstance();
            mng.setExamFile(examFile);
            mng.setCollegeFile(collegeFile);
            mng.setProctorsFile(proctorsFile);
            Stage stage = (Stage) nextButton.getScene().getWindow();
            viewFactory.showClassroomsTableWindow();
            viewFactory.closeStage(stage);
        }
    }

}
