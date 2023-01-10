package app;

import app.model.Classroom;

import java.io.File;
import java.util.List;

public class Manager {

    private static Manager instance = new Manager();
    private File examFile;
    private File collegeFile;
    private List<Classroom> selectedClassrooms;
    private List<Classroom> priorityClassrooms;

    private Manager() {}

    public static Manager getInstance() {
        if (null == instance)
            instance = new Manager();
        return instance;
    }

    public List<Classroom> getSelectedClassrooms() {
        return selectedClassrooms;
    }

    public void setSelectedClassrooms(List<Classroom> selectedClassrooms) {
        this.selectedClassrooms = selectedClassrooms;
    }

    public List<Classroom> getPriorityClassrooms() {
        return priorityClassrooms;
    }

    public void setPriorityClassrooms(List<Classroom> priorityClassrooms) {
        this.priorityClassrooms = priorityClassrooms;
    }

    public File getExamFile() {
        return examFile;
    }

    public void setExamFile(File examFile) {
        this.examFile = examFile;
    }

    public File getCollegeFile() {
        return collegeFile;
    }

    public void setCollegeFile(File collegeFile) {
        // Don't forget to delete the file after u done
        MinimizeCollegeSchedule obj = new MinimizeCollegeSchedule(collegeFile);
        this.collegeFile = obj.getNewFile();
    }
}
