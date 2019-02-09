package com.zach.notes.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
    private String noteFilter;
    private String oldText;

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
            setTitle(R.string.new_note); // This is a new note
        } else {
           // Retrieve the note, set the text.
            action = Intent.ACTION_EDIT;
            setTitle(R.string.edit_note);
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor noteCursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null, null);
            noteCursor.moveToFirst();
            oldText = noteCursor.getString(noteCursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            editorText.setText(oldText);
            editorText.requestFocus(); // Move to end and focus
        }
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
                if (newText.length() == 0) {
                    // Delete
                    deleteNote();
                } else if (!newText.equals(oldText)) {
                    updateNote(newText);
                    // Then we can save.
                } else {
                    setResult(RESULT_CANCELED); // No changes
                }
                break;
        }
        finish(); // Continue and go back to main
    }

    private void updateNote(String newText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, this.noteFilter, null );
        setResult(RESULT_OK);
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, this.noteFilter, null);
        setResult(RESULT_OK);
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
