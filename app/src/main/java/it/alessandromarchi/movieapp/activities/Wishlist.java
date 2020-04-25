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
    // Cursor movieItems;

    ListView movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        setTitle("Wishlist");

        movieDB = new MovieDB(this);

        movieList = findViewById(R.id.wishlist);
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
        Cursor movieItems = database.query(
                MovieTableHelper.TABLE_NAME,
                new String[]{
                        MovieTableHelper._ID,
                        MovieTableHelper.TITLE,
                        MovieTableHelper.IS_WISHLIST
                },
                MovieTableHelper.IS_WISHLIST + " = " + 1,
                null,
                null,
                null,
                null
        );

        movieItems.moveToNext();
        if (movieItems.getCount() >= 1) {
            if (movieAdapter == null) {
                movieAdapter = new MovieAdapter(this, movieItems);
                movieList.setAdapter(movieAdapter);
            } else {
                movieAdapter.changeCursor(movieItems);
                movieAdapter.notifyDataSetChanged();
            }
        }

        // movieItems.close();
        // database.close();
    }
}
