package it.alessandromarchi.movieapp.database;

import android.provider.BaseColumns;

public class MovieTableHelper implements BaseColumns {

    public static final String TABLE_NAME = "movies";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_PATH = "image_path";
    public static final String IS_SEEN = "is_seen";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            DESCRIPTION + " TEXT, " +
            IMAGE_PATH + " TEXT, " +
            IS_SEEN + " INTEGER DEFAULT 0);";
}
