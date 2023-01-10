package app.model;

import javafx.scene.control.CheckBox;

public class Classroom {
    private String room;
    private int capacity;
    private CheckBox isSelected, isPriority;

    public Classroom(String room, int capacity, String isSelectedValue, String isPriorityValue) {
        this.room = room;
        this.capacity = capacity;
        this.isSelected = new CheckBox();
        this.isPriority = new CheckBox();
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public CheckBox getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(CheckBox selected) {
        this.isSelected = selected;
    }

    public CheckBox getIsPriority() {
        return isPriority;
    }

    public void setIsPriority(CheckBox priority) {
        this.isPriority = priority;
    }

}