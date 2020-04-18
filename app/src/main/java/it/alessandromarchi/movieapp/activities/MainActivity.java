package it.alessandromarchi.movieapp.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.adapters.MovieAdapter;
import it.alessandromarchi.movieapp.database.MovieBD;
import it.alessandromarchi.movieapp.database.MovieTableHelper;

public class MainActivity extends AppCompatActivity {

    final String tableName = MovieTableHelper.TABLE_NAME;
    final String sortOrder = MovieTableHelper.TITLE + " ASC ";

    MovieAdapter movieAdapter;

    SQLiteDatabase database;
    MovieBD movieBD;
    Cursor movieItems;

    ListView movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieBD = new MovieBD(this);

        movieList = findViewById(R.id.movie_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        database = movieBD.getReadableDatabase();

        if (database != null) {
            loadMovies();
        } else {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
        }
    }

    private void loadMovies() {
        movieItems = database.query(tableName, null, null, null, null, null, sortOrder);

        if (movieItems != null) {
            if (movieAdapter == null) {
                movieAdapter = new MovieAdapter(this, movieItems);
                movieList.setAdapter(movieAdapter);
            } else {
                movieAdapter.changeCursor(movieItems);
                movieAdapter.notifyDataSetChanged();
            }
        }
    }
}
