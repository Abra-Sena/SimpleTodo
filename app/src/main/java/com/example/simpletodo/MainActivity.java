package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button addButton;
    EditText addItem;
    RecyclerView viewItem;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //custom action bar title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);

        addButton = findViewById(R.id.btnAdd);
        addItem = findViewById(R.id.dataItem);
        viewItem = findViewById(R.id.VItem);
        //add items to the model(no need of the mock data)
        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                //delete item from the model
                items.remove(position);
                //notify the adapter of which position the item was deleted
                itemsAdapter.notifyItemRemoved(position);
                //inform user that they have removed an item
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void OnClickListener(int position) {
                //to track click on an item
                Log.e("MainActivity", "Single click at position " + position);

                //create the new activity
                Intent edit = new Intent(MainActivity.this, EditActivity.class);
                //pass relevant data(that have been edited) to the other activity
                edit.putExtra(KEY_ITEM_TEXT, items.get(position));
                edit.putExtra(KEY_ITEM_POSITION, position);
                //tell system to display the other activity
                startActivityForResult(edit, EDIT_TEXT_CODE);
                //add animation between to go from main to edit activity
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        };

        //returns an items adapter
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        viewItem.setAdapter(itemsAdapter);
        //by default put things vertically on UI
        viewItem.setLayoutManager(new LinearLayoutManager(this));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = addItem.getText().toString();
                //add this new item to the model
                items.add(todoItem);
                //notify the adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size() - 1);
                //clear the edit test after it is submitted
                addItem.setText("");
                //inform user that submit was successful
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    //handle the result of edit activity (user's modification)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //retrieve updated test value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract original position of the edit item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update the model at the right position with the new item text
            items.set(position, itemText);
            //notify the adapter so that recycler view knows that a change was made
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item Updated Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("MainActivity", "Unknown call to onActivityResult");
        }
    }

    //persistence methods
    private File getDataFile() {
        return  new File(getFilesDir(), "data.txt");
    }

    //this function will load items by reading every line of data.txt file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading Items", e);
            items = new ArrayList<>();
        }
    }

    //this function save items by writing them into dat.txt file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing Items", e);
        }
    }
}