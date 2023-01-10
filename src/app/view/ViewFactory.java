package app.view;

import app.controller.BaseController;
import app.controller.ClassroomsTableController;
import app.controller.ResultWindowController;
import app.controller.UploadWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ViewFactory {

    public void showUploadWindow() {
        BaseController controller = new UploadWindowController(this, "UploadWindow.fxml");
        initializeStage(controller);
    }

    public void showClassroomsTableWindow() {
        BaseController controller = new ClassroomsTableController(this, "ClassroomsTableWindow.fxml");
        initializeStage(controller);
    }

    public void showResultWindow() {
        BaseController controller = new ResultWindowController(this, "ResultWindow.fxml");
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
