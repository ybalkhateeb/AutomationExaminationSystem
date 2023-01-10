package app;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinimizeCollegeSchedule {

    private File file;
    private File newFile;
    private List<String[]> records = new ArrayList<>();
    private List<String[]> newRecords = new ArrayList<>();

    public MinimizeCollegeSchedule(File file) {
        this.file = file;
    }

    public File getNewFile() {
        readFromOldFile();
        return newFile;
    }

    public void readFromOldFile() {

        try {
            FileReader fileReader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
            records = csvReader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int cnt = 0;
        for (int i = 0; i < records.size(); i++) {
            if (cnt == 0) {
                cnt++;
            } else {
                if ( (records.get(i)[2].equals(records.get(i-1)[2]))  && (records.get(i)[6].equals(records.get(i-1)[6])) && (records.get(i)[7].equals(records.get(i-1)[7]))) {
                    cnt++;
                    if (i == records.size()-1)
                        addNewRecord(records.get(i), i, cnt);
                } else {
                    addNewRecord(records.get(i), i, cnt);
                    cnt = 1;
                }
            }
        }
        addRecordsToNewFile(newRecords);
    }

    private void addNewRecord(String[] record, int i, int cnt) {
        record = Arrays.copyOf(records.get(i-1), records.get(i).length-1);
        record[record.length-1] = String.valueOf(cnt);
        newRecords.add(record);
    }
    private void addRecordsToNewFile(List<String[]> records){
        newFile = new File (file.getParent() + "/updatedCCSESchedule.csv");

        try {
            FileWriter outputFile= new FileWriter(newFile);
            CSVWriter csvWriter = new CSVWriter(outputFile);

            String[] header = {"SEMESTER_TERM_CODE", "SSBSECT_CAMPS_CODE", "CRS_TITLE", "INSTRUCTOR_ID", "INSTRUCTOR_NAME", "CRSE_SUBJCT", "CRSE_NUM", "CRN", "NoOfStudents"};
            csvWriter.writeNext(header);

            csvWriter.writeAll(records);
            csvWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
