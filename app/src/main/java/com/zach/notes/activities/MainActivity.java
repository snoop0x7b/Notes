package com.zach.notes.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zach.notes.R;
import com.zach.notes.controller.NoteCursorAdapter;
import com.zach.notes.model.NotesProvider;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cursorAdapter = new NoteCursorAdapter(this, null, 0);
        ListView list = findViewById( R.id.android_list);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,
                        EditActivity.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI+"/"+id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
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

    private void restartLoader() {
        getSupportLoaderManager().restartLoader(0, null,  this);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null); // blank it out.
    }

    public void openEditorNewNote(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // If everything looks good and we're sure we came back from the edit activity then we can refresh the data.
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader(); // Refresh data
        }
    }
}
