package com.zach.notes.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Note.class}, version = 2)
@TypeConverters(DateConverter.class)
public abstract class NoteDatabase extends RoomDatabase {


    private static NoteDatabase INSTANCE;
    private static final String databaseName = "notes.db";
    public abstract NoteDao noteDao();



    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.beginTransaction();
            database.execSQL("ALTER TABLE " + Note.TABLE_NAME + " DROP COLUMN noteCreated");
            database.execSQL("ALTER TABLE " + Note.TABLE_NAME + " ADD COLUMN created INTEGER DEFAULT 0");
            database.endTransaction();
        }
    };

    public static synchronized NoteDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            /*
             * TODO: Remove allowMainThreadQueries when the program goes multithreaded.
             */
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note-database")
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
