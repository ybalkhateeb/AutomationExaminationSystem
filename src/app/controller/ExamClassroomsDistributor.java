package app.controller;

import app.Manager;
import app.model.Classroom;
import app.model.StudentsExamSchedule;
import com.opencsv.CSVReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ExamClassroomsDistributor {

    private File examFile = Manager.getInstance().getExamFile();
    private File collegeFile = Manager.getInstance().getCollegeFile();
    private final List<Classroom> selectedClassrooms = Manager.getInstance().getSelectedClassrooms();
    private final List<Classroom> priorityClassrooms = Manager.getInstance().getPriorityClassrooms();
    private List<List<String>> examRecords = new ArrayList<>();
    private List<List<String>> collegeRecords = new ArrayList<>();
    static ObservableList<StudentsExamSchedule> studentsExamSchedule = FXCollections.observableArrayList();

    public static ObservableList<StudentsExamSchedule> getStudentsExamSchedule() {
        return studentsExamSchedule;
    }

    public void generateStudentsExamSchedule() {
        loadCSV(examFile, examRecords);
        loadCSV(collegeFile, collegeRecords);

        Map<String, Map<String, List<List<String>>>> ExamsGroupedRecords = examRecords.stream()
                .collect(Collectors.groupingBy(r -> r.get(2),
                        Collectors.groupingBy(r -> r.get(1),
                                Collectors.mapping(r -> r, Collectors.toList()))));

        for (Map.Entry<String, Map<String, List<List<String>>>> entry1 : ExamsGroupedRecords.entrySet()) {
            for (Map.Entry<String, List<List<String>>> entry2 : entry1.getValue().entrySet()) {
                for (List<String> record : entry2.getValue()) {
                    String[] split = record.get(0).split("-");
                    String CRSE_SUBJECT = split[0];
                    String CRSE_NUM = split[1];

                    for (List<String> row : collegeRecords) {
                        if (row.get(5).equals(CRSE_SUBJECT) && row.get(6).equals(CRSE_NUM)) {
                            int noOfStudents = Integer.parseInt(row.get(8));
                            String classroom = findSuitableClassroom(priorityClassrooms, selectedClassrooms, noOfStudents);
                            if (classroom != null) {
                                studentsExamSchedule.add(new StudentsExamSchedule(record.get(0), row.get(7), classroom, record.get(1), record.get(2), (record.get(2).equals("1")) ? "09:00 AM - 11:00 AM" : "01:00 PM - 03:00 PM"));
                            } else {
                                // generate error, or make it empty
                            }
                        }
                    }
                }
            }
        }
    }

    private String findSuitableClassroom(List<Classroom> priorityClassrooms, List<Classroom> selectedClassrooms, int noOfStudents) {
        int firstBiggestCapacity = 400;
        for (Classroom classroom : priorityClassrooms) {
            if (classroom.getCapacity() < firstBiggestCapacity && classroom.getCapacity() >= noOfStudents) {
                priorityClassrooms.remove(classroom);
                return classroom.getRoom();
            }
        }
        for (Classroom classroom : selectedClassrooms) {
            if (classroom.getCapacity() < firstBiggestCapacity && classroom.getCapacity() >= noOfStudents) {
                selectedClassrooms.remove(classroom);
                return classroom.getRoom();
            }
        }
        return null;
    }

    public void loadCSV(File file, List<List<String>> records) {
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            reader.readNext(); // Skip the first line
            String[] values;
            while ((values = reader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
