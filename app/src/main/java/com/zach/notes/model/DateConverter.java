package com.zach.notes.model;
import android.arch.persistence.room.TypeConverter;
import java.util.Date;

/**
 * Provides the ability to convert from date to long
 */
public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
