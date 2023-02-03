package app.controller;

import app.model.StudentsExamSchedule;
import app.view.ViewFactory;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.poi.xslf.util.PDFFontMapper;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.AbstractCell;
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
    private TableColumn<StudentsExamSchedule, String> courseIDColumn;
    @FXML
    private TableColumn<StudentsExamSchedule, String> dateColumn;
    @FXML
    private TableColumn<StudentsExamSchedule, String> roomColumn;
    @FXML
    private TableColumn<StudentsExamSchedule, String> sectionColumn;
    @FXML
    private TableColumn<StudentsExamSchedule, String> sessionColumn;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<StudentsExamSchedule, String> timeColumn;
    @FXML
    private Button exportButton;

    public ResultWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       courseIDColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("course_id"));
       dateColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("date"));
       roomColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("room"));
       sessionColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("session"));
       sectionColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("section"));
       timeColumn.setCellValueFactory(new PropertyValueFactory<StudentsExamSchedule, String>("time"));

       tableView.setItems(ExamClassroomsDistributor.getStudentsExamSchedule());

    }

    @FXML
    void prevButtonAction() {
        Stage stage = (Stage) exportButton.getScene().getWindow();
        viewFactory.showClassroomsTableWindow();
        viewFactory.closeStage(stage);
    }

    @FXML
    void exportButtonAction() throws IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            String filePath = file.getAbsolutePath();
            saveSystem(new File(filePath + ".pdf"));
        }
    }

    private void saveSystem(File file) throws IOException, ParseException {

        final Color BLUE_LIGHT_1 = new Color(186, 206, 230);
        final Color YELLOW_LIGHT_1 = new Color(255, 255, 153);
        ObservableList<StudentsExamSchedule> scheduleData = tableView.getItems();
        String currentDate = scheduleData.get(0).getDate();
        String currentCourseID = scheduleData.get(0).getCourse_id();
        StringBuilder roomAndCRN = new StringBuilder();
        StringBuilder sections = new StringBuilder();

        try (PDDocument document = new PDDocument()) {
            final PDRectangle pageSize = new PDRectangle(1000, 1000);
            final PDPage page = new PDPage(pageSize);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                Table.TableBuilder tableBuilder = Table.builder()
                        .addColumnsOfWidth(80, 200, 400, 80, 200);

                // add titles
                tableBuilder.addRow(createCollegeTitle());
                tableBuilder.addRow(createBranchTitle());
                tableBuilder.addRow(createTypeTitle());

                // add note
                tableBuilder.addRow(createENGNoteHeader());

                // add title date
                tableBuilder.addRow(createDateHeader(currentDate));

                // add header
                tableBuilder.addRow(createHeaders());

                // add data
                for (int i = 0; i < scheduleData.size(); i++) {

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
                    String room = ((String) scheduleData.get(i).getRoom().getValue()).substring(0,4);
                    String section = "("+scheduleData.get(i).getSection().substring(0,4) + "), ";
                    roomAndCRN.append(room + section);
                    sections.append(scheduleData.get(i).getSection().substring(0,4) + ",");

                    if (!scheduleData.get(i).getDate().trim().equals(currentDate.trim())) {
                        tableBuilder.addRow(createDateHeader(scheduleData.get(i).getDate()));
                        tableBuilder.addRow(createHeaders());
                        currentDate = scheduleData.get(i).getDate();
                    }

                }

                // adding the last rows
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
            }
            document.save(file);
        }
    }

    private Row createTypeTitle() {
        return (Row.builder()
                .add(TextCell.builder().text("Type").fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(2).paddingBottom(5).build())
                .add(TextCell.builder().text("Final Exam " + Year.now()).fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(3).paddingLeft(170).paddingBottom(5).build())
                .build());
    }

    private Row createBranchTitle() {
        return (Row.builder()
                .add(TextCell.builder().text("Branch").fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(2).paddingBottom(5).build())
                .add(TextCell.builder().text("Male").fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(3).paddingLeft(200).paddingBottom(5).build())
                .build());
    }

    private Row createCollegeTitle() {
        return (Row.builder()
                .add(TextCell.builder().text("College").fontSize(14)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(2).paddingBottom(5).build())
                .add(TextCell.builder().text("Computer Science and Engineering").fontSize(20)
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(0).colSpan(3).paddingLeft(50).paddingBottom(5).build())
                .build());
    }

    private static Row createHeaders() {
        final Color BLUE_LIGHT_2 = new Color(45, 118, 187);
        return Row.builder()
                .add(TextCell.builder().text("Course ID").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                .add(TextCell.builder().text("Section CRN").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                .add(TextCell.builder().text("Room (CRN)").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                .add(TextCell.builder().text("Session").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                .add(TextCell.builder().text("Time").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1).padding(8).build())
                .textColor(BLUE_LIGHT_2)
                .build();
    }

    private static Row createDateHeader(String strDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date parsedDate = sdf.parse(strDate);
        DateFormat format = new SimpleDateFormat("EEEE");
        String dayOfWeek = format.format(parsedDate);
        return Row.builder()
                .add(TextCell.builder().borderWidth(1).padding(6)
                        .colSpan(5)
                        .text(dayOfWeek + " " + strDate)
                        .horizontalAlignment(HorizontalAlignment.CENTER).build())
                .backgroundColor(Color.lightGray)
                .fontSize(13)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .build();

    }
    private Row createENGNoteHeader() {
        return Row.builder()
                .add(TextCell.builder().borderWidth(1).padding(6)
                        .colSpan(5)
                        .text("Note: You should know your course CRN, and be seated in the right room.")
                        .horizontalAlignment(HorizontalAlignment.CENTER).build())
                .backgroundColor(Color.WHITE)
                .textColor(Color.RED)
                .fontSize(13)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .build();
    }

    private Row createARNoteHeader() {
        String phrase = "ملاحظة: يجب معرفة الرقم المرجعي CRN لشعبتك والتوجه للقاعه المناسبة.";
        return Row.builder()
                .add(TextCell.builder()
                        .colSpan(5)
                        .text(phrase)
                        .horizontalAlignment(HorizontalAlignment.CENTER).build())
                .backgroundColor(Color.WHITE)
                .textColor(Color.RED)
                .fontSize(13)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .build();
    }
}
