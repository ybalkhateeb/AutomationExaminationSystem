package app.model;

import app.Manager;
import com.opencsv.CSVReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Scheduler {

    private final File examsFile = Manager.getInstance().getExamFile();
    private final File collegeFile = Manager.getInstance().getCollegeFile();
    private final File proctorsFile = Manager.getInstance().getProctorsFile();
    private final List<Classroom> selectedClassrooms = Manager.getInstance().getSelectedClassrooms();
    private final List<Classroom> priorityClassrooms = Manager.getInstance().getPriorityClassrooms();
    private final List<List<String>> examRecords = new ArrayList<>();
    private final List<List<String>> collegeRecords = new ArrayList<>();
    private final List<List<String>> proctorsRecords = new ArrayList<>();
    private final List<List<String>> proctorsRecordsTmp = new ArrayList<>();
    private static String semester = "";
    static ObservableList<ExamSchedule> studentsExamSchedule = FXCollections.observableArrayList();

    public static ObservableList<ExamSchedule> getStudentsExamSchedule() {
        return studentsExamSchedule;
    }

    public static String getSemester() {
        return semester;
    }

    public void startDistributing2() {
        loadCSV(examsFile, examRecords);
        loadCSV(collegeFile, collegeRecords);
        loadCSV(proctorsFile, proctorsRecords);
        semester = collegeRecords.get(0).get(0);

        Map<String, Map<String, List<List<String>>>> ExamsGroupedRecords = examRecords.stream()
                .collect(Collectors.groupingBy(r -> r.get(2),
                        Collectors.groupingBy(r -> r.get(1),
                                Collectors.mapping(r -> r, Collectors.toList()))));

        ExamsGroupedRecords = new TreeMap<>(ExamsGroupedRecords);


    }

    public void startDistributing() {
        loadCSV(examsFile, examRecords);
        loadCSV(collegeFile, collegeRecords);
        loadCSV(proctorsFile, proctorsRecords);
        loadCSV(proctorsFile, proctorsRecordsTmp);

        semester = collegeRecords.get(0).get(0);

        Map<String, Map<String, List<List<String>>>> ExamsGroupedRecords = examRecords.stream()
                .collect(Collectors.groupingBy(r -> r.get(2),
                        Collectors.groupingBy(r -> r.get(1),
                                Collectors.mapping(r -> r, Collectors.toList()))));

        ExamsGroupedRecords = new TreeMap<>(ExamsGroupedRecords);
        Collections.sort(priorityClassrooms, Comparator.comparingInt(Classroom ::getCapacity));
        Collections.sort(selectedClassrooms, Comparator.comparingInt(Classroom ::getCapacity));

        for (Map.Entry<String, Map<String, List<List<String>>>> entry1 : ExamsGroupedRecords.entrySet()) {
            Set<String> chosenProctors = new HashSet<>();
            Set<Classroom> chosenClassrooms = new HashSet<>();

            for (Map.Entry<String, List<List<String>>> entry2 : entry1.getValue().entrySet()) {
                // loop over exams
                for (List<String> record : entry2.getValue()) {
                    List<List<String>> sections = new ArrayList<>();
                    String[] split = record.get(0).split("-");
                    String CRSE_SUBJECT = split[0];
                    String CRSE_NUM = split[1];

                    for (List<String> collegeData : collegeRecords) {
                        // find a match between subject in exams and college schedule
                        if (collegeData.get(5).equals(CRSE_SUBJECT) && collegeData.get(6).equals(CRSE_NUM))
                            // add section of the course to this array
                            sections.add(collegeData);
                    }

                    List<Classroom>  courseClassrooms= new ArrayList<>();
                    for (List<String> section : sections) {

                        String noOfStudents = section.get(8);
                        String sectionAndCapacity = section.get(7) + " (" + noOfStudents + ")";

                        Classroom classroom = findSuitableClassroom(priorityClassrooms, selectedClassrooms, Integer.parseInt(noOfStudents), chosenClassrooms, courseClassrooms);
                        if (classroom != null) {
                            String proctor = findSuitableProctor(chosenProctors);
                            courseClassrooms.add(classroom);
                            String timeSlot = switch (record.get(1)) {
                                case "1" -> "09:00 AM - 11:00 AM";
                                case "2" -> "01:00 PM - 03:00 PM";
                                default -> "Invalid Time Slot";
                            };
                            ExamSchedule obj = new ExamSchedule(record.get(0), sectionAndCapacity, FXCollections.observableArrayList(getSelectedClassroomsList()), FXCollections.observableArrayList(getProctorsNames()), record.get(1), record.get(2), timeSlot);
                            obj.setDefaultRoom((classroom != null) ? classroom.getRoom() + " (" + classroom.getCapacity() + ")" : "Choose a room");
                            obj.setDefaultProctor(proctor);
                            studentsExamSchedule.add(obj);
                        }
                        else {
                            String proctor1 = findSuitableProctor(chosenProctors);
                            String proctor2 = findSuitableProctor(chosenProctors);
                            int halfNumStudents = Integer.parseInt(noOfStudents) / 2;
                            Classroom classroom1 = findSuitableClassroom(priorityClassrooms, selectedClassrooms, halfNumStudents, chosenClassrooms, courseClassrooms);
                            Classroom classroom2 = findSuitableClassroom(priorityClassrooms, selectedClassrooms, halfNumStudents, chosenClassrooms, courseClassrooms);
                            String timeSlot = switch (record.get(1)) {
                                case "1" -> "09:00 AM - 11:00 AM";
                                case "2" -> "01:00 PM - 03:00 PM";
                                default -> "Invalid Time Slot";
                            };
                            ExamSchedule obj1 = new ExamSchedule(record.get(0), sectionAndCapacity, FXCollections.observableArrayList(getSelectedClassroomsList()), FXCollections.observableArrayList(getProctorsNames()), record.get(1), record.get(2), timeSlot);
                            obj1.setDefaultRoom((classroom1 != null) ? classroom1.getRoom() + " (" + classroom1.getCapacity() + ")" : "Choose a room");
                            obj1.setDefaultProctor(proctor1);
                            studentsExamSchedule.add(obj1);

                            ExamSchedule obj2 = new ExamSchedule(record.get(0), sectionAndCapacity, FXCollections.observableArrayList(getSelectedClassroomsList()), FXCollections.observableArrayList(getProctorsNames()), record.get(1), record.get(2), timeSlot);
                            obj2.setDefaultRoom((classroom2 != null) ? classroom2.getRoom() + " (" + classroom2.getCapacity() + ")" : "Choose a room");
                            obj2.setDefaultProctor(proctor2);
                            studentsExamSchedule.add(obj2);
                        }
                    }
                    courseClassrooms.clear();
                }

                for (Classroom classroom : chosenClassrooms) {
                    classroom.setRemainingCapacityToDefault();
                }
                chosenClassrooms.clear();
                chosenProctors.clear();
            }
        }

    }

    // this need refactoring
    private Classroom findSuitableClassroom(List<Classroom> priorityClassrooms, List<Classroom> selectedClassrooms,
                                            int noOfStudents, Set<Classroom> sessionClassrooms, List<Classroom> courseClassrooms) {

        for (Classroom classroom : priorityClassrooms) {
            if (classroom.canAccommodate(noOfStudents) &&
                    (courseClassrooms.contains(classroom) || !(sessionClassrooms.contains(classroom))) &&
                    !(classroom.getRoom().equals("Online"))) {
                classroom.addSection(noOfStudents);
                sessionClassrooms.add(classroom);
                return classroom;
            }
        }
        for (Classroom classroom : selectedClassrooms) {
            if (classroom.canAccommodate(noOfStudents) &&
                    (courseClassrooms.contains(classroom) || !(sessionClassrooms.contains(classroom))) &&
                    !(classroom.getRoom().equals("Online"))) {
                classroom.addSection(noOfStudents);
                sessionClassrooms.add(classroom);
                return classroom;
            }
        }
        return null;
    }

    private String findSuitableProctor(Set<String> chosenProctors) {
        for (int i = 0; i < proctorsRecordsTmp.size(); i++) {
               String name = proctorsRecords.get(i).get(2);
                int teaching_hours = Integer.parseInt(proctorsRecordsTmp.get(i).get(3));
                int office_hours = Integer.parseInt(proctorsRecordsTmp.get(i).get(4));
                int total_hours = teaching_hours + office_hours;

                if (total_hours < 19 && !(chosenProctors.contains(name))) {
                    chosenProctors.add(name);
                    proctorsRecordsTmp.get(i).set(4, String.valueOf(office_hours+2));
                    return name + " (" + (Integer.parseInt(proctorsRecords.get(i).get(4)) + Integer.parseInt(proctorsRecords.get(i).get(3))) + ")";
                }
            }
        return "Choose a proctor";
    }

    private List<String> getProctorsNames() {
        List<String> myList = new ArrayList<>();
        for (List<String> row : proctorsRecords) {
            myList.add(row.get(2) + "( " + (Integer.parseInt(row.get(3)) + Integer.parseInt(row.get(4))) + ")");
        }
        return myList;
    }

    private List<String> getSelectedClassroomsList() {
        List<String> myList = new ArrayList<>();
        for (Classroom row : selectedClassrooms) {
           myList.add(row.getRoom() + " (" + row.getCapacity() + ")");
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
