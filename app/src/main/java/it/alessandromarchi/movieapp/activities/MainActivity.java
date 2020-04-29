package it.alessandromarchi.movieapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.adapters.MovieAdapter;
import it.alessandromarchi.movieapp.database.MovieDB;
import it.alessandromarchi.movieapp.database.MovieProvider;
import it.alessandromarchi.movieapp.database.MovieTableHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final String tableName = MovieTableHelper.TABLE_NAME;
    private static final int LOADER_ID = 568175;

    SQLiteDatabase database;
    MovieDB movieDB;
    Cursor movies;
    MovieAdapter movieAdapter;

    GridView moviesGrid;
    MenuItem action_wishlist;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        action_wishlist = menu.getItem(0);
        action_wishlist.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent wishlist = new Intent(MainActivity.this, Wishlist.class);
                startActivity(wishlist);

                return true;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieDB = new MovieDB(this);
        movieAdapter = new MovieAdapter(this, null);

        moviesGrid = findViewById(R.id.movies_grid);
        moviesGrid.setAdapter(movieAdapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent movieDetail = new Intent(MainActivity.this, MovieDetail.class);
                movieDetail.putExtra("movie_id", id);
                startActivity(movieDetail);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        movieAdapter.notifyDataSetChanged();

//        database = movieDB.getReadableDatabase();
//
//        if (database != null) {
//            loadMovies();
//        } else {
//            Toast.makeText(this, R.string.database_error, Toast.LENGTH_LONG).show();
//        }
    }

//    private void loadMovies() {
//        movies = database.query(tableName, null, null, null, null, null, null);
//
//        if (movies != null) {
//            if (movieAdapter == null) {
//                movieAdapter = new MovieAdapter(this, movies);
//                moviesGrid.setAdapter(movieAdapter);
//            } else {
//                movieAdapter.changeCursor(movies);
//                movieAdapter.notifyDataSetChanged();
//            }
//        }
//
//        database.close();
//    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, MovieProvider.MOVIES_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        movieAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        movieAdapter.changeCursor(null);
    }
}
