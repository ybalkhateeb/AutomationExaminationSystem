package app.model;

public class StudentsExamSchedule {
    private String course_id;
    private String section;
    private String room; // should be like choosing
    private String session;
    private String date;
    private String time;

    public StudentsExamSchedule(String course_id, String section, String room, String session, String date, String time) {
        this.course_id = course_id;
        this.section = section;
        this.room = room;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
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
