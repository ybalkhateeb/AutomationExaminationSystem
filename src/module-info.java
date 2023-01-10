module AutomatedExaminationSystem2 {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swt;
    requires opencsv;

    opens app;
    opens app.controller;
    opens app.view;
    opens app.model;
}