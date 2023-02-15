package app.model;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class ExamSchedule {
    private String course_id;
    private String section;
    private ComboBox rooms;
    private ComboBox proctors;
    private String session;
    private String date;
    private String time;


    public ExamSchedule(String course_id, String section, ObservableList rooms, ObservableList proctors, String session, String date, String time) {
        this.course_id = course_id;
        this.section = section;
        this.rooms = new ComboBox(rooms);
        this.proctors = new ComboBox(proctors);
        this.session = session;
        this.date = date;
        this.time = time;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public ComboBox getRooms() {
        return rooms;
    }

    public void setRooms(ComboBox rooms) {
        this.rooms = rooms;
    }

    public void setDefaultRoom(String DefaultRoom) {
        rooms.getSelectionModel().select(DefaultRoom);
    }

    public ComboBox getProctors() {
        return proctors;
    }

    public void setProctors(ComboBox proctors) {
        this.proctors = proctors;
    }
    
    public void setDefaultProctor(String DefaultProctor) {
        proctors.getSelectionModel().select(DefaultProctor);
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
