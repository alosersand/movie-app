package it.alessandromarchi.movieapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MovieDB extends SQLiteOpenHelper {
    public static final String DB_NAME = "movies.db";
    public static final int VERSION = 1;

    public MovieDB(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieTableHelper.CREATE);

        for (int i = 0; i < 20; i++) {
            ContentValues values = new ContentValues();

            values.put(MovieTableHelper.TITLE, "Title N°" + i);
            values.put(MovieTableHelper.IS_SEEN, i % 3 == 1 ? 1 : 0);

            db.insert(MovieTableHelper.TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
