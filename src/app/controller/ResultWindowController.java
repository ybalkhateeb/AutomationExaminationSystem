package app.controller;

import app.model.ExamSchedule;
import app.view.ViewFactory;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.ResourceBundle;

public class ResultWindowController extends BaseController implements Initializable {

    @FXML
    private Button exportBtnForStudents;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<ExamSchedule, String> courseIDColumn;
    @FXML
    private TableColumn<ExamSchedule, String> dateColumn;
    @FXML
    private TableColumn<ExamSchedule, String> roomColumn;
    @FXML
    private TableColumn<ExamSchedule, String> proctorColumn;
    @FXML
    private TableColumn<ExamSchedule, String> sectionColumn;
    @FXML
    private TableColumn<ExamSchedule, String> sessionColumn;
    @FXML
    private TableColumn<ExamSchedule, String> timeColumn;

    final Color BLUE_LIGHT_1 = new Color(186, 206, 230);
    final Color YELLOW_LIGHT_1 = new Color(255, 255, 153);

    public ResultWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       courseIDColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("course_id"));
       dateColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("date"));
       roomColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("rooms"));
       proctorColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("proctors"));
       sessionColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("session"));
       sectionColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("section"));
       timeColumn.setCellValueFactory(new PropertyValueFactory<ExamSchedule, String>("time"));

       tableView.setItems(Scheduler.getStudentsExamSchedule());
    }

    @FXML
    void prevButtonAction() {
        tableView.getItems().clear();
        Stage stage = (Stage) exportBtnForStudents.getScene().getWindow();
        viewFactory.showClassroomsTableWindow();
        viewFactory.closeStage(stage);
    }

    @FXML
    void exportBtnForStudentsAction() throws IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null)
            createFileForStudents(new File(file.getAbsoluteFile() + ".pdf"));
    }

    @FXML
    void exportBtnForProctorsAction() throws IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null)
            createFileForProctors(new File(file.getAbsoluteFile() + ".pdf"));
    }

    private void createFileForProctors(File file) throws IOException, ParseException {
        ObservableList<ExamSchedule> scheduleData = tableView.getItems();
        String currentDate = scheduleData.get(0).getDate();

        try (PDDocument document = new PDDocument()) {
            final PDRectangle pageSize = new PDRectangle(1000, 1000);
            PDPage page = new PDPage(pageSize);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            Table.TableBuilder tableBuilder = Table.builder()
                    .addColumnsOfWidth(130, 150, 150, 250, 80, 200);

            createTitles("proctors", tableBuilder, currentDate);

            for (int i = 0; i < scheduleData.size(); i++) {

                // new date
                if (!scheduleData.get(i).getDate().trim().equals(currentDate.trim())) {
                    page = new PDPage(pageSize);
                    document.addPage(page);
                    contentStream.close();
                    contentStream = new PDPageContentStream(document, page);

                    tableBuilder = Table.builder()
                        .addColumnsOfWidth(130, 150, 150, 250, 80, 200);

                    currentDate = scheduleData.get(i).getDate();
                    createTitles("proctors", tableBuilder, currentDate);
                }

                String proctor = String.valueOf(scheduleData.get(i).getProctors().getValue());
                String room = String.valueOf(scheduleData.get(i).getRooms().getValue());
                room = room.equals("Choose a room") ? "" : room.replaceAll("\\(\\d+\\)", "");
                tableBuilder.addRow(Row.builder()
                        .add(TextCell.builder().text(scheduleData.get(i).getCourse_id()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                        .add(TextCell.builder().text(scheduleData.get(i).getSection().substring(0,4)).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                        .add(TextCell.builder().text(room).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                        .add(TextCell.builder().text(proctor.substring(0,proctor.length()-4)).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                        .add(TextCell.builder().text(scheduleData.get(i).getSession()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                        .add(TextCell.builder().text(scheduleData.get(i).getTime()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                        .backgroundColor(scheduleData.get(i).getSession().equals("1") ? BLUE_LIGHT_1 : YELLOW_LIGHT_1)
                        .build());

                TableDrawer tableDrawer = TableDrawer.builder()
                        .page(page)
                        .contentStream(contentStream)
                        .startX(20f)
                        .startY(page.getMediaBox().getUpperRightY()-20f)
                        .table(tableBuilder.build())
                        .build();

                tableDrawer.draw();
            }
        contentStream.close();
        document.save(file);
        }
    }


    private void createFileForStudents(File file) throws IOException, ParseException {
        ObservableList<ExamSchedule> scheduleData = tableView.getItems();
        String currentDate = scheduleData.get(0).getDate();
        String currentCourseID = scheduleData.get(0).getCourse_id();
        StringBuilder roomAndCRN = new StringBuilder();
        StringBuilder sections = new StringBuilder();

        try (PDDocument document = new PDDocument()) {
            int cnt = 0;
            final PDRectangle pageSize = new PDRectangle(1000, 1000);
            PDPage page = new PDPage(pageSize);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            Table.TableBuilder tableBuilder = Table.builder()
                    .addColumnsOfWidth(80, 200, 400, 80, 200);


            createTitles("students", tableBuilder, currentDate);
            tableBuilder.addRow(createDateRow(currentDate, "students"));
            tableBuilder.addRow(createHeaders("students"));


            for (int i = 0; i < scheduleData.size(); i++) {

                // same date
                if (!scheduleData.get(i).getCourse_id().equals(currentCourseID)) {
                    tableBuilder.addRow(Row.builder()
                            .add(TextCell.builder().text(scheduleData.get(i-1).getCourse_id()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                            .add(TextCell.builder().text(String.valueOf(sections.deleteCharAt(sections.length()-1))).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                            .add(TextCell.builder().text(String.valueOf(roomAndCRN.deleteCharAt(roomAndCRN.length()-2))).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                            .add(TextCell.builder().text(scheduleData.get(i-1).getSession()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                            .add(TextCell.builder().text(scheduleData.get(i-1).getTime()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                            .backgroundColor(scheduleData.get(i-1).getSession().equals("1") ? BLUE_LIGHT_1 : YELLOW_LIGHT_1)
                            .build());

                    currentCourseID = scheduleData.get(i).getCourse_id();
                    roomAndCRN = new StringBuilder();
                    sections = new StringBuilder();
                }

                String room = ((String) scheduleData.get(i).getRooms().getValue());
                room = room.equals("Choose a room") ? "" : room.replaceAll("\\(\\d+\\)", "");
                String section = "("+scheduleData.get(i).getSection().substring(0,4) + "), ";
                roomAndCRN.append(room + section);
                sections.append(scheduleData.get(i).getSection().substring(0,4) + ",");

                // new date
                if (!scheduleData.get(i).getDate().trim().equals(currentDate.trim())) {
                    cnt++;
                    // after 2 dates, add a new page
                    if (cnt == 2) {

                        TableDrawer tableDrawer = TableDrawer.builder()
                                .page(page)
                                .contentStream(contentStream)
                                .startX(20f)
                                .startY(page.getMediaBox().getUpperRightY()-20f)
                                .table(tableBuilder.build())
                                .build();
                        tableDrawer.draw();

                        tableBuilder = Table.builder()
                                .addColumnsOfWidth(80, 200, 400, 80, 200);

                        page = new PDPage(pageSize);
                        document.addPage(page);
                        contentStream.close();
                        contentStream = new PDPageContentStream(document, page);

                        createTitles("students", tableBuilder, currentDate);
                        cnt = 0;
                    }
                    // add date and header for the new date
                    tableBuilder.addRow(createDateRow(scheduleData.get(i).getDate(), "students"));
                    tableBuilder.addRow(createHeaders("students"));
                    currentDate = scheduleData.get(i).getDate();
                }

            }

            //adding last row
            tableBuilder.addRow(Row.builder()
                    .add(TextCell.builder().text(scheduleData.get(scheduleData.size()-1).getCourse_id()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                    .add(TextCell.builder().text(String.valueOf(sections.deleteCharAt(sections.length()-1))).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                    .add(TextCell.builder().text(String.valueOf(roomAndCRN.deleteCharAt(roomAndCRN.length()-2))).horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                    .add(TextCell.builder().text(scheduleData.get(scheduleData.size()-1).getSession()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                    .add(TextCell.builder().text(scheduleData.get(scheduleData.size()-1).getTime()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).paddingTop(10).paddingBottom(10).build())
                    .backgroundColor(scheduleData.get(scheduleData.size()-1).getSession().equals("1") ? BLUE_LIGHT_1 : YELLOW_LIGHT_1)
                    .build());

            TableDrawer tableDrawer = TableDrawer.builder()
                    .page(page)
                    .contentStream(contentStream)
                    .startX(20f)
                    .startY(page.getMediaBox().getUpperRightY()-20f)
                    .table(tableBuilder.build())
                    .build();

            tableDrawer.draw();
            contentStream.close();
            document.save(file);
        }
    }

    private void createTitles(String type, Table.TableBuilder tableBuilder, String date) throws ParseException {
        if (type.equals("students")) {
            tableBuilder.addRow(createCollegeTitle("students"));
            tableBuilder.addRow(createBranchTitle("students"));
            tableBuilder.addRow(createTypeTitle("students"));
            tableBuilder.addRow(createNoteRow("ENG"));
        }
        else {
            tableBuilder.addRow(createCollegeTitle("proctors"));
            tableBuilder.addRow(createBranchTitle("proctors"));
            tableBuilder.addRow(createTypeTitle("proctors"));
            tableBuilder.addRow(createDateRow(date, "proctors"));
            tableBuilder.addRow(createHeaders("proctors"));
        }

    }

    private Row createTypeTitle(String type) {
        return (Row.builder()
                .add(TextCell.builder().text("Type").fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0)
                        .colSpan(2).paddingBottom(5).build())
                .add(TextCell.builder().text("Final Exam " + Year.now()).fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(type.equals("students") ? 3 : 4).paddingLeft(170).paddingBottom(5).build())
                .build());

    }

    private Row createBranchTitle(String type) {
        return (Row.builder()
                .add(TextCell.builder().text("Branch").fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(2).paddingBottom(5).build())
                .add(TextCell.builder().text("Male").fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(type.equals("students") ? 3 : 4).paddingLeft(200).paddingBottom(5).build())
                .build());
    }

    private Row createCollegeTitle(String type) {
        return (Row.builder()
                .add(TextCell.builder().text("College").fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(2).paddingBottom(5).build())
                .add(TextCell.builder().text("Computer Science and Engineering").fontSize(20)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(type.equals("students") ? 3 : 4).paddingLeft(50).paddingBottom(5).build())
                .build());
    }

    private Row createHeaders(String type) {
        final Color BLUE_LIGHT_2 = new Color(45, 118, 187);
        if (type.equals("students")) {
            return Row.builder()
                    .add(TextCell.builder().text("Course ID").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Section CRN").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Room (CRN)").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Session").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Time").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .textColor(BLUE_LIGHT_2)
                    .build();
        }
        else {
            return Row.builder()
                    .add(TextCell.builder().text("Course ID").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Section CRN").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Room").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Proctor").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Session").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .add(TextCell.builder().text("Time").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                    .textColor(BLUE_LIGHT_2)
                    .build();
        }
    }

    private Row createDateRow(String strDate, String type) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date parsedDate = sdf.parse(strDate);
        DateFormat format = new SimpleDateFormat("EEEE");
        String dayOfWeek = format.format(parsedDate);
        return Row.builder()
                .add(TextCell.builder().borderWidth(1).padding(6)
                        .colSpan(type.equals("students") ? 5 : 6)
                        .text(dayOfWeek + " " + strDate)
                        .horizontalAlignment(HorizontalAlignment.CENTER).build())
                .backgroundColor(Color.lightGray)
                .fontSize(13)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .build();
    }

    private Row createNoteRow(String lang) {
        return Row.builder()
                .add(TextCell.builder().borderWidth(1).padding(6)
                        .colSpan(5)
                        .text(lang.equals("ENG") ? "Note: You should know your course CRN, and be seated in the right room."
                                : "ملاحظة: يجب معرفة الرقم المرجعي CRN لشعبتك والتوجه للقاعه المناسبة.")
                        .horizontalAlignment(HorizontalAlignment.CENTER).build())
                .backgroundColor(Color.WHITE)
                .textColor(Color.RED)
                .fontSize(13)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .build();
    }

}

