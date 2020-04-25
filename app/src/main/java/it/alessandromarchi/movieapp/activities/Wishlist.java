package it.alessandromarchi.movieapp.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.adapters.MovieAdapter;
import it.alessandromarchi.movieapp.database.MovieDB;
import it.alessandromarchi.movieapp.database.MovieTableHelper;

public class Wishlist extends AppCompatActivity {

    MovieAdapter movieAdapter;

    SQLiteDatabase database;
    MovieDB movieDB;
    Cursor movies;

    ListView moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        setTitle("Wishlist");

        movieDB = new MovieDB(this);

        moviesList = findViewById(R.id.movies_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        database = movieDB.getReadableDatabase();

        if (database != null) {
            loadWishList();
        } else {
            Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
        }
    }

    private void loadWishList() {
        movies = database.query(
                MovieTableHelper.TABLE_NAME,
                /*new String[]{
                        MovieTableHelper._ID,
                        MovieTableHelper.TITLE,
                        MovieTableHelper.IS_WISHLIST
                },*/
                null,
                MovieTableHelper.IS_WISHLIST + " = " + 1,
                null,
                null,
                null,
                null
        );

        movies.moveToNext();
        if (movies.getCount() >= 1) {

            if (movieAdapter == null) {
                movieAdapter = new MovieAdapter(this, movies);
                moviesList.setAdapter(movieAdapter);
            } else {
                movieAdapter.changeCursor(movies);
                movieAdapter.notifyDataSetChanged();
            }
        }

        // movies.close();
        database.close();
    }
}
