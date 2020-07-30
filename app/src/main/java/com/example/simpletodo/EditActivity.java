package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.simpletodo.MainActivity.KEY_ITEM_POSITION;
import static com.example.simpletodo.MainActivity.KEY_ITEM_TEXT;

public class EditActivity extends AppCompatActivity {

    EditText editItem;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editItem = findViewById(R.id.editItem);
        btnSave = findViewById(R.id.btnSave);

        //descriptive title to indicate to user what activity they are doing
        getSupportActionBar().setTitle("Edit Item");
        //copy item's data from main activity to edit activity to allow user make modifications
        editItem.setText(getIntent().getStringExtra(KEY_ITEM_TEXT));

        //add on click listener on save button to save user modifications
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an intent which will contain the result of user's modifications
                Intent update =  new Intent();

                //pass the updated data
                update.putExtra(KEY_ITEM_TEXT, editItem.getText().toString());
                update.putExtra(KEY_ITEM_POSITION, getIntent().getExtras().getInt(KEY_ITEM_POSITION));

                //set the result of the intent
                setResult(RESULT_OK, update);
                //finish edit activity, which means close the screen and go back to main page
                finish();
            }
        });
    }
}