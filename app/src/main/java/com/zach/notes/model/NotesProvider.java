package com.zach.notes.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NotesProvider extends ContentProvider {

    private static final String AUTHORITY = "com.zach.notes.notesprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Note";

    private SQLiteDatabase database;
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH+"/#", NOTES_ID); // Particular note
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * Perform a note query
     * @param uri - Note URI
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return Returns a cursor for the query result
     */
    public Cursor query( Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {

        final Context context = getContext();
        if (context == null) {
            // An error has occurred.
            throw new IllegalStateException("No context found");
        }
        NoteDao noteDao = NoteDatabase.getInstance(context).noteDao();
        if (uriMatcher.match(uri) == NOTES_ID) {
            // If the note URI has a note ID
             int noteId = Integer.parseInt(uri.getLastPathSegment()); // Last path is the ID.
             return noteDao.findById(noteId);
        } else {
            return noteDao.getNotes();
        }
    }

    @Override
    public String getType( Uri uri) {
        return null;
    }

    @Override
    public Uri insert( Uri uri,  ContentValues values) {
        final Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("No context found");
        }
        final long id = NoteDatabase.getInstance(context).noteDao()
                .insert(Note.fromContentValues(values));
        return Uri.parse(BASE_PATH+"/"+id);
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        final Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("No context found");
        }
        if (uriMatcher.match(uri) == NOTES_ID) {
            // If the note URI has a note ID
            int noteId = Integer.parseInt(uri.getLastPathSegment()); // Last path is the ID.
            return NoteDatabase.getInstance(context).noteDao().deleteById(noteId);
        } else if (uriMatcher.match(uri) == NOTES) {
            return NoteDatabase.getInstance(context).noteDao().deleteAll();
        }
        return 0;
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        switch(uriMatcher.match(uri)) {
            case NOTES_ID:
                final Context context = getContext();
                if (context == null) {
                    throw new IllegalStateException("Context is null");
                }
                final Note note = Note.fromContentValues(values);
                long noteId = Long.parseLong(uri.getLastPathSegment());
                note.setId(noteId);
                return NoteDatabase.getInstance(context).noteDao()
                        .update(note);
            default:
            case NOTES:
                throw new IllegalArgumentException("Invalid URI can't update without an ID " + uri);

        }
    }


}
