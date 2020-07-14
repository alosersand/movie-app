package it.alessandromarchi.moviest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MovieDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "movies.db";
    private static final int VERSION = 1;

    public MovieDB(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieTableHelper.CREATE);
        db.execSQL(WishlistTableHelper.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
