package models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToDoItemDao {
    @Query("SELECT * FROM todolist")
    List<ToDoItem> listAll();

    @Insert
    void insert(ToDoItem toDoItem);

    @Insert
    void insertAll(ToDoItem... toDoItems);

    @Query("UPDATE todolist set toDoItemIsComplete = :isComplete where toDoItemID = :id")
    void updateIsComplete(int id, Boolean isComplete);

    @Query("DELETE FROM todolist")
    void deleteAll();
}
