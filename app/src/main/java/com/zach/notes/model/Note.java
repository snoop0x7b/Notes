package com.zach.notes.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;

import java.util.Date;

@Entity(tableName = "notes")
public class Note {

    //Constants for identifying table and columns
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_CREATED = "created";

    public static final String[] ALL_COLUMNS = {NOTE_ID, NOTE_TEXT, NOTE_CREATED};

    @Ignore
    public static final String TABLE_NAME = "notes";
    /*
    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_CREATED = "noteCreated";
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private Long id;

    @ColumnInfo(name="noteText")
    private String noteText;

    @ColumnInfo(name="created")
    private Date noteCreated;

    @Ignore
    public Note() {

    }
    @Ignore
    public Note(String noteText) {
        this.noteText = noteText;
        noteCreated = new Date();
    }

    public Note(Long id, String noteText, Date noteCreated) {
        this.id = id;
        this.noteCreated = noteCreated;
        this.noteText = noteText;
    }
    /**
     * Create a new {@link Note} from the specified {@link ContentValues}.
     *
     * @param values A {@link ContentValues} that at least contain {@link #NOTE_TEXT}.
     * @return A newly created {@link Note} instance.
     */
    public static Note fromContentValues(ContentValues values) {
        final Note note = new Note();
        if (values.containsKey(NOTE_ID)) {
            note.id = values.getAsLong(NOTE_ID);
        }
        if (values.containsKey(NOTE_TEXT)) {
            note.noteText = values.getAsString(NOTE_TEXT);
        }
        note.noteCreated = new Date();
        return note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public Date getNoteCreated() {
        return noteCreated;
    }

    public void setNoteCreated(Date noteCreated) {
        this.noteCreated = noteCreated;
    }
}
