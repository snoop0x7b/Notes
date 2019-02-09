package com.zach.notes.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

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
        editorText = findViewById(R.id.editText);

        // Pass back the data
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
        // Default behavior is new note
        action = Intent.ACTION_INSERT;
        setTitle(R.string.new_note);
        if (uri != null) {
           // Retrieve the note, set the text.
            Cursor noteCursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null, null);
            if (noteCursor != null) {
                action = Intent.ACTION_EDIT;
                setTitle(R.string.edit_note);
                noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
                noteCursor.moveToFirst();
                oldText = noteCursor.getString(noteCursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
                editorText.setText(oldText);
                editorText.requestFocus(); // Move to end and focus
                noteCursor.close();
            } // It's improbable that this is null. But defensive coding is a best practice
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
        Toast.makeText(this,getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, this.noteFilter, null);
        Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String newText) {
        /*
         * Wrapper for insert to take a string and just do the insert
         * @param noteValue
         * @return returns the URI for the new note
         */
         ContentValues values = new ContentValues();
         values.put(DBOpenHelper.NOTE_TEXT, newText);
         Uri result = getContentResolver().insert(NotesProvider.CONTENT_URI,
                  values);
         if (result != null) {
             setResult(RESULT_OK);
         } else {
             setResult(100); // Error
         }
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
