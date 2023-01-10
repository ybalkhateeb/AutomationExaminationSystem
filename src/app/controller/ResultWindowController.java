package app.controller;

import app.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ResultWindowController extends BaseController {


    @FXML
    private TableColumn<?, ?> dateColumn;

    @FXML
    private Button exportButton;

    @FXML
    private TableColumn<?, ?> roomColumn;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> timeColumn;


    public ResultWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @FXML
    void exportButtonAction() {

    }

}
