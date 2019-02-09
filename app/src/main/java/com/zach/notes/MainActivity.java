package com.zach.notes;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.support.v4.content.CursorLoader;
import android.content.DialogInterface;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.zach.notes.controller.NoteCursorAdapter;
import com.zach.notes.model.DBOpenHelper;
import com.zach.notes.model.NotesProvider;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String[] from = {DBOpenHelper.NOTE_TEXT};
        int[] to = { R.id.tvNote };

        cursorAdapter = new NoteCursorAdapter(this, null, 0);
        ListView list = (ListView) findViewById( R.id.android_list);
        list.setAdapter(cursorAdapter);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private Uri createNote(String noteValue) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteValue);
        Uri result = getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);
        Log.d("MainActivity", "Inserted Note "  + result.getLastPathSegment());
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_create_data:
                createNotes();
                break;
            case R.id.action_delete_data:
                deleteNotesCmd();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Confirm prior to doing the delete CMD
     */
    private void deleteNotesCmd() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Which is which button
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.all_deleted),
                            Toast.LENGTH_SHORT).show();
                    deleteAllNotes();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void deleteAllNotes() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, null,null);
        restartLoader();
    }

    private void createNotes() {
        for(int i = 0; i < 5; i++) {
            createNote("Aasdfasdf asdf asdf adsf blah blah " + i);
        }
        for(int i = 0; i < 5; i++) {
            createNote("Aasdfasdf asdf asdf \n adsf blah blah" + i);
        }
        restartLoader();
    }

    private void restartLoader() {
        getSupportLoaderManager().restartLoader(0, null,  this);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null); // blank it out.
    }
}