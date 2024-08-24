package models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.util.Date;

@Entity(tableName = "todolist")
public class ToDoItem {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "toDoItemID")
    private int toDoItemID;

    @ColumnInfo(name = "toDoItemName")
    private String toDoItemName;

    @ColumnInfo(name = "toDoItemDueDate")
    private Long toDoItemDueDate;

    @ColumnInfo(name = "toDoItemCategory")
    private String toDoItemCategory;

    @ColumnInfo(name = "toDoItemIsComplete")
    private Boolean toDoItemIsComplete;

    public ToDoItem(String toDoItemName){
        this.toDoItemName = toDoItemName;
    }

    public ToDoItem(Item item) {
        this.toDoItemName = item.getName();
        this.toDoItemDueDate = item.getDueDate().getTime();
        this.toDoItemCategory = item.getCategory().name();
        this.toDoItemIsComplete = item.getIsComplete();
    }

    public int getToDoItemID() {
        return toDoItemID;
    }

    public void setToDoItemID(int toDoItemID) {
        this.toDoItemID = toDoItemID;
    }

    public String getToDoItemName() {
        return toDoItemName;
    }

    public void setToDoItemName(String toDoItemName) {
        this.toDoItemName = toDoItemName;
    }

    public Long getToDoItemDueDate() {
        return toDoItemDueDate;
    }

    public String getToDoItemCategory() {
        return toDoItemCategory;
    }

    public Boolean getToDoItemIsComplete() {
        return toDoItemIsComplete;
    }

    public void setToDoItemDueDate(Long toDoItemDueDate) {
        this.toDoItemDueDate = toDoItemDueDate;
    }

    public void setToDoItemCategory(String toDoItemCategory) {
        this.toDoItemCategory = toDoItemCategory;
    }

    public void setToDoItemIsComplete(Boolean toDoItemIsComplete) {
        this.toDoItemIsComplete = toDoItemIsComplete;
    }
}
