package comp5216.sydney.edu.au.todolist;

import static helper.Utils.formatDate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;

import models.Category;
import models.Item;


public class EditToDoItemActivity extends Activity
{
    public int position=0;
    EditText etItem;
    TextView textViewDueDate;
    Button btnDueDate;
    Item item;
    Spinner spinnerCategory;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Populate the screen using the layout
        setContentView(R.layout.activity_edit_item);

        // Get the data from the main activity screen
        // String editItem = getIntent().getStringExtra("item");
        Item editItem = (Item) getIntent().getSerializableExtra("item");

        // Setup spinner
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Category.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Setup date picker
        textViewDueDate = findViewById(R.id.textViewDueDate);
        btnDueDate = findViewById(R.id.btnDueDate);

        calendar = Calendar.getInstance();

        // If not empty, insert original text into text field
        if (editItem  != null) {
            item = editItem;
            position = getIntent().getIntExtra("position",-1);

            // Show original content in all fields
            etItem = (EditText)findViewById(R.id.etEditItem);
            etItem.setText(item.getName());
            int categoryPosition = adapter.getPosition(item.getCategory());
            spinnerCategory.setSelection(categoryPosition);
            calendar.setTime(item.getDueDate());
            textViewDueDate.setText(formatDate(calendar.getTime()));
        }

        btnDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
    }

    private void datePicker() {
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH);
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    timePicker();
                }, curYear, curMonth, curDay);

        datePickerDialog.show();
    }

    private void timePicker() {
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hour, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    textViewDueDate.setText(formatDate(calendar.getTime()));
                }, curHour, curMinute, true);

        timePickerDialog.show();
    }

    public void onSubmit(View v) {
        etItem = (EditText) findViewById(R.id.etEditItem);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        // Prepare data intent for sending it back
        Intent data = new Intent();

        if (item == null) {
            item = new Item(0, etItem.getText().toString(), Category.valueOf(spinnerCategory.getSelectedItem().toString()), calendar.getTime(), Boolean.FALSE);
        }

        item.setName(etItem.getText().toString());
        item.setCategory(Category.valueOf(spinnerCategory.getSelectedItem().toString()));
        item.setDueDate(calendar.getTime());

        data.putExtra("item", item);
        data.putExtra("position", position);

        // Activity finishes OK, return the data
        setResult(RESULT_OK, data); // Set result code and bundle data for response
        finish(); // Close the activity, pass data to parent
    }

    public void onCancel(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditToDoItemActivity.this);
        builder.setTitle(R.string.dialog_cancel_edit_title)
                .setMessage(R.string.dialog_cancel_edit_msg)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish(); // Close the activity
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User cancelled the dialog
                        // Nothing happens
                    }
                });
        builder.create().show();
    }
}