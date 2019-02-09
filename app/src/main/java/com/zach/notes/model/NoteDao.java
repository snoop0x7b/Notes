package com.zach.notes.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * from " + Note.TABLE_NAME)
    Cursor getNotes();

    @Query("SELECT * FROM " + Note.TABLE_NAME+ " WHERE _id = :id")
    Cursor findById(Integer id);

    @Insert
    long[] insertAll(Note ... notes);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM " + Note.TABLE_NAME + " WHERE " + Note.NOTE_ID + " = :id")
    int deleteById(int id);

    @Query("DELETE FROM " + Note.TABLE_NAME)
    int deleteAll();

    @Insert
    long insert(Note fromContentValues);

    @Update
    int update(Note note);
}
