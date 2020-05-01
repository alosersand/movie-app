package it.alessandromarchi.movieapp.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

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

public class Wishlist extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 871462;

    MovieDB movieDB;
    MovieAdapter movieAdapter;

    ListView moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        setTitle(R.string.wishlist);

        movieDB = new MovieDB(this);
        movieAdapter = new MovieAdapter(this, null);

        moviesList = findViewById(R.id.movies_list);
        moviesList.setAdapter(movieAdapter);

        //TODO aggiornare con metodo non deprecato
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        movieAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                this,
                MovieProvider.MOVIES_URI,
                null,
                MovieTableHelper.IS_WISHLIST + " = " + 1,
                null,
                null
        );
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
