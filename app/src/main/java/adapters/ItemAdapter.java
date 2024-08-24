package adapters;

import static helper.Utils.formatDate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import comp5216.sydney.edu.au.todolist.MainActivity;
import comp5216.sydney.edu.au.todolist.R;
import models.Item;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit ;

/** Custom adapter for Item Class*/

public class ItemAdapter extends ArrayAdapter<Item> {
    private final Context context;

    public ItemAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item, parent, false);
        }

        // Lookup view for data population
        TextView titleTextView = convertView.findViewById(R.id.txtName);
        TextView dueDateTextView = convertView.findViewById(R.id.txtDueDate);
        TextView timeUntilDueTextView = convertView.findViewById(R.id.txtTimeUntilDue);
        CheckBox isCompleteCheckBox = convertView.findViewById(R.id.checkBoxIsComplete);

        // Populate the data into the template view using the data object
        titleTextView.setText(item.getName());
        dueDateTextView.setText(formatDate(item.getDueDate()));

        Long timeUntilDue = item.getDueDate().getTime() - new Date().getTime(); // Time to due date in milliseconds
        // Adapted from https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
        String timeUntilDueStr = String.format("%02d days, %02d hours, %02d minutes",
                TimeUnit.MILLISECONDS.toDays(timeUntilDue),
                TimeUnit.MILLISECONDS.toHours(timeUntilDue) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeUntilDue)),
                TimeUnit.MILLISECONDS.toMinutes(timeUntilDue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeUntilDue)));
        timeUntilDueStr = "Due in: " + timeUntilDueStr;
        timeUntilDueTextView.setText(timeUntilDueStr);

        if (item.getDueDate().before(new Date())) {
            dueDateTextView.setText(R.string.overdue);
        }

        // Set a listener for the checkbox to update the isComplete field
        isCompleteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setIsComplete(isChecked);
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateItemIsCompleteToDatabase(item);
            }
            Log.i("Item checked: ", item.toString());
        });

        isCompleteCheckBox.setChecked(item.getIsComplete());

        // Return the completed view to render on screen
        return convertView;
    }
}
