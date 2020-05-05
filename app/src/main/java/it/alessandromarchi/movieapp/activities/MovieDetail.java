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
	TextView description;

    SQLiteDatabase database;
    MovieDB movieDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = findViewById(R.id.detail_title);
			description = findViewById(R.id.detail_description);

        movieDB = new MovieDB(this);
        database = movieDB.getReadableDatabase();

        Intent intent = getIntent();
        long id = intent.getLongExtra("movie_id", 0);

        Cursor movies = database.query(
                MovieTableHelper.TABLE_NAME,
                new String[]{
												MovieTableHelper.DESCRIPTION,
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
					title.setText(movies.getString(movies.getColumnIndex(MovieTableHelper.TITLE)));
					description.setText(movies.getString(movies.getColumnIndex(MovieTableHelper.DESCRIPTION)));

            setTitle(movies.getString(movies.getColumnIndex(MovieTableHelper.TITLE)));
        } else {
            title.setText(R.string.database_read_error);
            setTitle(R.string.app_name);
        }

        movies.close();
        database.close();
    }
}
