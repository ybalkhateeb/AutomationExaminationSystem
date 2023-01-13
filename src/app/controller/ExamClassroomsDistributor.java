package app.controller;

import app.Manager;
import app.model.Classroom;
import com.opencsv.CSVReader;

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

    public void start() {
        loadCSV(examFile, examRecords);
        loadCSV(collegeFile, collegeRecords);
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

    public void printRecords() {
       for (List<String> row : collegeRecords) {
           for (String item : row)
               System.out.print(item + ",");
           System.out.println();
       }
        System.out.println( " -------------------------------------- ");
        for (List<String> row : examRecords) {
            for (String item : row)
                System.out.print(item + ",");
            System.out.println();
        }
    }

}
