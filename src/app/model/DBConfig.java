package app.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.sql.*;

public class DBConfig {

    static Connection connection = null;

    public static Connection connectDB() throws SQLException, ClassNotFoundException {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ics411", "root", "root1234");
            return connection;
    }

    public static ObservableList<Classroom> getDataClassrooms() throws SQLException, ClassNotFoundException {
        Connection connection = connectDB();
        ObservableList<Classroom> list = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = connection.prepareStatement("select * from Classrooms") ;
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Classroom(rs.getString("room"), Integer.parseInt(rs.getString("capacity")), rs.getString("isSelected"), rs.getString("isPriority")));
            }
        } catch (Exception e) {

        }

        return list;
    }

}
