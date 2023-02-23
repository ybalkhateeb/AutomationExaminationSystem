package app.controller;

import app.model.Classroom;
import app.model.DBConfig;
import app.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddClassroomController extends BaseController {

    public AddClassroomController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @FXML
    private TextField capacityField;
    @FXML
    private TextField roomField;
    @FXML
    private Text errorLabel;
    @FXML
    private Button saveClassroomBtn;

    String query = "";
    Connection connection = null;
    ResultSet resultSet = null;
    PreparedStatement preparedStatement;
    Classroom classroom;

    @FXML
    void saveClassroomBtnAction() throws SQLException, ClassNotFoundException {
        connection = DBConfig.connectDB();
        String room = roomField.getText();
        String capacity = capacityField.getText();

        if (room.isEmpty() || capacity.isEmpty())
            errorLabel.setText("Please Fill All DATA");
        else {

            // capacity must be a number, check it
            if (!capacity.matches("\\d*")) {
                errorLabel.setText("Capacity field should be a number");
                return;
            }

            getQuery();
            insert();


            Stage stage = (Stage) saveClassroomBtn.getScene().getWindow();
            stage.close();
        }
    }

    private void getQuery() {
        query = "INSERT INTO classrooms(room, capacity, isSelected, isPriority) VALUES (?,?,?,?)";
    }

    private void insert() {

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, roomField.getText());
            preparedStatement.setInt(2, Integer.parseInt(capacityField.getText()));
            preparedStatement.setString(3, "");
            preparedStatement.setString(4, "");
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTextField(String room, int capacity){
        roomField.setText(room);
        capacityField.setText(String.valueOf(capacity));
    }
}
