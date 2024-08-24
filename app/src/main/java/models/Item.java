package models;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {

    private int id;
    private String name;
    private Date dueDate;
    private Category category;
    private Boolean isComplete;

    public Item(int id, String name, Category category, Date dueDate, Boolean isComplete) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.dueDate = dueDate;
        this.isComplete = isComplete;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Boolean isComplete) {
        this.isComplete = isComplete;
    }

    @Override
    public String toString() {
        return id + " | " + name + " (Due: " + dueDate + ", Category: " + category + ", Complete: " + isComplete + ")";
    }
}
