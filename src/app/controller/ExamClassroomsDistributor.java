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

    private final File examFile = Manager.getInstance().getExamFile();
    private final File collegeFile = Manager.getInstance().getCollegeFile();
    private final List<Classroom> selectedClassrooms = Manager.getInstance().getSelectedClassrooms();
    private final List<Classroom> priorityClassrooms = Manager.getInstance().getPriorityClassrooms();
    private final List<List<String>> examRecords = new ArrayList<>();
    private final List<List<String>> collegeRecords = new ArrayList<>();
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

        ExamsGroupedRecords = new TreeMap<>(ExamsGroupedRecords);
        Collections.sort(priorityClassrooms, Comparator.comparingInt(Classroom ::getCapacity));
        Collections.sort(selectedClassrooms, Comparator.comparingInt(Classroom ::getCapacity));
        Set<Classroom> chosenClassrooms = new HashSet<>();

        for (Map.Entry<String, Map<String, List<List<String>>>> entry1 : ExamsGroupedRecords.entrySet()) {
            for (Map.Entry<String, List<List<String>>> entry2 : entry1.getValue().entrySet()) {
                for (List<String> record : entry2.getValue()) {
                    String[] split = record.get(0).split("-");
                    String CRSE_SUBJECT = split[0];
                    String CRSE_NUM = split[1];

                    for (List<String> collegeData : collegeRecords) {
                        if (collegeData.get(5).equals(CRSE_SUBJECT) && collegeData.get(6).equals(CRSE_NUM)) {
                            String noOfStudents = collegeData.get(8);
                            Classroom classroom = findSuitableClassroom(priorityClassrooms, selectedClassrooms, Integer.parseInt(noOfStudents), chosenClassrooms);
                            String sectionAndCapacity = collegeData.get(7) + " (" + noOfStudents + ")";
                            String timeSlot = switch (record.get(1)) {
                                case "1" -> "09:00 AM - 11:00 AM";
                                case "2" -> "01:00 PM - 03:00 PM";
                                default -> "Invalid Time Slot";
                            };
                            StudentsExamSchedule obj = new StudentsExamSchedule(record.get(0), sectionAndCapacity, FXCollections.observableArrayList(getSelectedClassroomsList()), record.get(1), record.get(2), timeSlot);
                            obj.setDefaultRoom((classroom != null) ? classroom.getRoom() + " (" + classroom.getCapacity() + ")" : "Choose a room");
                            studentsExamSchedule.add(obj);
                        }
                    }
                }
                chosenClassrooms.clear();
            }
        }
    }

    // this need refactoring
    private Classroom findSuitableClassroom(List<Classroom> priorityClassrooms, List<Classroom> selectedClassrooms, int noOfStudents, Set<Classroom> chosenClassrooms) {
        int firstBiggestCapacity = 400;

        for (Classroom classroom : priorityClassrooms) {
            if (classroom.getCapacity() < firstBiggestCapacity && classroom.getCapacity() >= noOfStudents && !(chosenClassrooms.contains(classroom))) {
                chosenClassrooms.add(classroom);
                return classroom;
            }
        }
        for (Classroom classroom : selectedClassrooms) {
            if (classroom.getCapacity() < firstBiggestCapacity && classroom.getCapacity() >= noOfStudents && !(chosenClassrooms.contains(classroom))) {
                chosenClassrooms.add(classroom);
                return classroom;
            }
        }
        return null;
    }

    private List<String> getSelectedClassroomsList() {
        List<String> myList = new ArrayList<>();
        for (Classroom lst : selectedClassrooms) {
           myList.add(lst.getRoom() + " (" + lst.getCapacity() + ")");
        }

        return myList;
    }

    private void loadCSV(File file, List<List<String>> records) {
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
