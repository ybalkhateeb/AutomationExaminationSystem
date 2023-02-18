package app.view;

import app.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ViewFactory {

    public void showUploadView() {
        BaseController controller = new UploadController(this, "UploadView.fxml");
        initializeStage(controller);
    }

    public void showClassroomSelectionView() {
        BaseController controller = new ClassroomSelectionController(this, "ClassroomSelectionView.fxml");
        initializeStage(controller);
    }

    public void showExamScheduleView() {
        BaseController controller = new ExamScheduleController(this, "ExamScheduleView.fxml");
        initializeStage(controller);
    }


    private void initializeStage(BaseController baseController) {
        String fxmlName =  baseController.getFxmlName();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlName));
        fxmlLoader.setController(baseController);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(parent);
        Stage stage = new Stage();

        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }

    public void closeStage(Stage stageToClose) {
        stageToClose.close();
    }


}
