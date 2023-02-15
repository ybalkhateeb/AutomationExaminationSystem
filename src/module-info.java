module AutomatedExaminationSystem2 {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swt;
    requires opencsv;
    requires org.apache.poi.examples;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.scratchpad;
    requires org.apache.xmlbeans;
    requires org.apache.pdfbox;
    requires org.apache.fontbox;
    requires easytable;
    requires java.desktop;
    requires com.ibm.icu;

    opens app;
    opens app.controller;
    opens app.view;
    opens app.model;
}