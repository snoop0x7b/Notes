package com.zach.notes.controller;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.TextView;

import com.zach.notes.R;
import com.zach.notes.model.Note;

public class NoteCursorAdapter extends CursorAdapter {

    /**
     *
     * @param context
     * @param c
     * @param flags
     */
    public NoteCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     *
     * @param context
     * @param cursor
     * @param parent
     * @return Returns an inflated version of the note list we set up.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
         // Use the note list XML file we created earlier.
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
    }

    /**
     * Adapts our note text field and truncates it. Setting it up in the text box in our text view in note list item
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String noteText =cursor.getString(
                cursor.getColumnIndex(Note.NOTE_TEXT)
        );

        int pos = noteText.indexOf("\n"); // Returns -1 if not found, something else if found.
        if (pos != -1) {
            noteText = noteText.substring(0, pos) + "..."; // Truncate
        }// Found
        TextView tv = view.findViewById(R.id.tvNote);
        tv.setText(noteText);
    }
}
