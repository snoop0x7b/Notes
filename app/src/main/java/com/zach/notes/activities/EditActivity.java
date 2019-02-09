package com.zach.notes.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.zach.notes.R;
import com.zach.notes.model.DBOpenHelper;
import com.zach.notes.model.NotesProvider;

public class EditActivity extends AppCompatActivity {

    // Determines what we're doing. Edit or insert
    private String action;
    private EditText editorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // Hook up my editorText
        editorText = (EditText) findViewById(R.id.editText);

        // Pass back the data
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(R.string.new_note);
        } // This is a new note
    }

    // Called when the user exits this activity
    private void saveNote() {
        String newText = editorText.getText().toString().trim();
        switch(action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                break;
        }
        finish(); // Continue and go back to main
    }

    private void insertNote(String newText) {
        /**
         * Wrapper for insert to take a string and just do the insert
         * @param noteValue
         * @return returns the URI for the new note
         */
         ContentValues values = new ContentValues();
         values.put(DBOpenHelper.NOTE_TEXT, newText);
         Uri result = getContentResolver().insert(NotesProvider.CONTENT_URI,
                  values);
         Log.d("EditActivity", "Inserted Note "  + result.getLastPathSegment());
         setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        saveNote();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                saveNote();
                break;
        }
        return true;
    }
}
