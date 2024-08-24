package comp5216.sydney.edu.au.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import adapters.ItemAdapter;
import models.Category;
import models.Item;
import models.ToDoItem;
import models.ToDoItemDB;
import models.ToDoItemDao;


public class MainActivity extends AppCompatActivity {

    // Define variables
    ListView listView;
    ArrayList<Item> items;
    ItemAdapter itemsAdapter;
    ToDoItemDB db;
    ToDoItemDao toDoItemDao;
    ActivityResultLauncher<Intent> addNewLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);
        // Reference the "listView" variable to the id "lstView" in the layout
        listView = (ListView) findViewById(R.id.lstView);

        // Init db
        db = ToDoItemDB.getDatabase(this.getApplication().getApplicationContext());
        toDoItemDao = db.toDoItemDao();
        readItemsFromDatabase();

        // Create an adapter for the list view using Android's built-in item layout
        itemsAdapter = new ItemAdapter(this, items);
        listView.setAdapter(itemsAdapter);

        addNewLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Item toAddItem = (Item) result.getData().getSerializableExtra("item");
                        Log.i("test", toAddItem + "");
                        if (toAddItem != null) {
                            Log.i("Added item in list ", toAddItem + ", position: " + toAddItem);

                            // Make a standard toast that just contains text
                            Toast.makeText(getApplicationContext(), "Added: " + toAddItem, Toast.LENGTH_SHORT).show();
                            items.add(toAddItem);
                            sortItems();
                            itemsAdapter.notifyDataSetChanged();
                        }
                        Log.i("done notify: ", items.toString());
                        saveItemsToDatabase();
                    }
                }
        );
        setupListViewListener();
    }

    public void onAddNewClick(View view) {
        // Register a request to start an activity for result and register the result callback
        Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
        if (intent != null) {
            // brings up the second activity
            addNewLauncher.launch(intent);
        }
    }

    private void setupListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long rowId) {
                Log.i("MainActivity", "Long Clicked item " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                        items.remove(position); // Remove item from the ArrayList
                                        itemsAdapter.notifyDataSetChanged(); // Notify listView adapter to update the list
                                        saveItemsToDatabase();
                                }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                                    }
                                });
                builder.create().show();
                return true;
            }
        });

        // Register a request to start an activity for result and register the result callback
        ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Extract name value from result extras
                        // String editedItem = result.getData().getExtras().getString("item");
                        Item editedItem = (Item) result.getData().getSerializableExtra("item");
                        int position = result.getData().getIntExtra("position", -1);
                        items.set(position, editedItem);
                        Log.i("Updated item in list ", editedItem + ", position: " + position);

                        // Make a standard toast that just contains text
                        Toast.makeText(getApplicationContext(), "Updated: " + editedItem, Toast.LENGTH_SHORT).show();

                        sortItems();
                        itemsAdapter.notifyDataSetChanged();
                        saveItemsToDatabase();
                    }
                }
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item updateItem = (Item) itemsAdapter.getItem(position);
                Log.i("MainActivity", "Clicked item " + position + ": " + updateItem);
                Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the edit activity
                    intent.putExtra("item", updateItem);
                    intent.putExtra("position", position);
                    // brings up the second activity
                    mLauncher.launch(intent);
                    itemsAdapter.notifyDataSetChanged();
                }
            }

        });
    }

    private void readItemsFromDatabase() {
        //Use asynchronous task to run query on the background and wait for result
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //read items from database
                    List<ToDoItem> itemsFromDB = toDoItemDao.listAll();
                    items = new ArrayList<Item>();
                    if (itemsFromDB != null && itemsFromDB.size() > 0) {
                        for (ToDoItem item : itemsFromDB) {
                            Item tmp_item = new Item(item.getToDoItemID(), item.getToDoItemName(), Category.valueOf(item.getToDoItemCategory()), new Date(item.getToDoItemDueDate()), item.getToDoItemIsComplete()); //todo: update
                            items.add(tmp_item);
                            Log.i( "SQLite read item" , "ID: " + item.getToDoItemID() + " Name: " + item.getToDoItemName());
                        }
                    }
                    sortItems();
                    System.out.println( "I'll run in a separate thread than the main thread.");
                }
            });
            // Block and wait for the future to complete
            future.get();
        }
        catch (Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }

    private void saveItemsToDatabase() {
        //Use asynchronous task to run query on the background to avoid locking UI
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //delete all items and re-insert
                    toDoItemDao.deleteAll();
                    for (Item item : items ) {
                        ToDoItem sql_item = new ToDoItem(item);
                        toDoItemDao.insert(sql_item);
                        Log.i( "SQLite saved item" , item.toString());
                    }
                }
            });
            System.out.println( "I'll run in a separate thread than the main thread.");
            // Block and wait for the future to complete
            future.get();
        }
        catch (Exception ex) {
            Log.e("saveItemsToDatabase", ex.getStackTrace().toString());
        }
    }

    public void updateItemIsCompleteToDatabase(Item item) {
        //Use asynchronous task to run query on the background to avoid locking UI
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    toDoItemDao.updateIsComplete(item.getId(), item.getIsComplete());
                }
            });
            System.out.println( "I'll run in a separate thread than the main thread (update isComplete).");
            // Block and wait for the future to complete
            future.get();
        }
        catch (Exception ex) {
            Log.e("updateItemIsCompleteToDatabase", ex.getStackTrace().toString());
        }
    }

    private void sortItems() {
        items.sort(Comparator.comparing(Item::getIsComplete).thenComparing(Item::getDueDate));
        Log.i("done sort: ", items.toString());
    }

}