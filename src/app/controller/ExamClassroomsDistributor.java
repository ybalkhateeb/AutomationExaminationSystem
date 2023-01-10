package app.controller;

import app.Manager;
import app.model.Classroom;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExamClassroomsDistributor {

    private File examFile = Manager.getInstance().getExamFile();
    private File collegeFile = Manager.getInstance().getCollegeFile();
    private List<Classroom> selectedClassrooms = Manager.getInstance().getSelectedClassrooms();
    private List<Classroom> priorityClassrooms = Manager.getInstance().getPriorityClassrooms();
    private List<List<String>> examRecords = new ArrayList<>();
    private List<List<String>> collegeRecords = new ArrayList<>();

    public void start() {
        loadExamCSV(examFile);
        loadCollegeCSV(collegeFile);
        printRecords();
    }
    public void loadExamCSV(File file) {
        System.out.println("loading exam csv");
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
           String[] values;
           while ((values = reader.readNext()) != null) {
               examRecords.add(Arrays.asList(values));
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCollegeCSV(File file) {
        System.out.println("load college csv");
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] values;
            while ((values = reader.readNext()) != null) {
                collegeRecords.add(Arrays.asList(values));
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
