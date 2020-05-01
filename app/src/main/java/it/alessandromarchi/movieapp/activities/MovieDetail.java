package it.alessandromarchi.movieapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.database.MovieDB;
import it.alessandromarchi.movieapp.database.MovieTableHelper;

public class MovieDetail extends AppCompatActivity {

    TextView title;

    SQLiteDatabase database;
    MovieDB movieDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = findViewById(R.id.detail_title);

        movieDB = new MovieDB(this);
        database = movieDB.getReadableDatabase();

        Intent intent = getIntent();
        long id = intent.getLongExtra("movie_id", 0);

        Cursor movies = database.query(
                MovieTableHelper.TABLE_NAME,
                new String[]{
                        MovieTableHelper.IS_WISHLIST,
                        MovieTableHelper.TITLE,
                        MovieTableHelper._ID
                },
                MovieTableHelper._ID + " = " + id,
                null,
                null,
                null,
                null
        );

        movies.moveToNext();
        if (movies.getCount() >= 1) {
            title.setText(getString(R.string.TMP, movies.getInt(movies.getColumnIndex(MovieTableHelper.IS_WISHLIST))));
            setTitle(movies.getString(movies.getColumnIndex(MovieTableHelper.TITLE)));
        } else {
            title.setText(R.string.database_read_error);
            setTitle(R.string.app_name);
        }

        movies.close();
        database.close();
    }
}
