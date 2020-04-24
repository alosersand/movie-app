package it.alessandromarchi.movieapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.adapters.MovieAdapter;
import it.alessandromarchi.movieapp.database.MovieDB;
import it.alessandromarchi.movieapp.database.MovieTableHelper;

public class MainActivity extends AppCompatActivity {

    final String tableName = MovieTableHelper.TABLE_NAME;
    final String sortOrder = MovieTableHelper.TITLE + " ASC ";

    MovieAdapter movieAdapter;

    SQLiteDatabase database;
    MovieDB movieDB;
    Cursor movieItems;

    GridView movieGrid;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieDB = new MovieDB(this);

        movieGrid = findViewById(R.id.movie_grid);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent movieDetail = new Intent(MainActivity.this, MovieDetail.class);
                movieDetail.putExtra("movie_id", id);
                startActivity(movieDetail);
            }
        });

//        movieGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Cursor movie = (Cursor) movieAdapter.getItem(position);
//
//                ContentValues values = new ContentValues();
//
//                values.put(MovieTableHelper.IS_SEEN, 1);
//
//
//                return true;
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        database = movieDB.getReadableDatabase();

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
                movieGrid.setAdapter(movieAdapter);
            } else {
                movieAdapter.changeCursor(movieItems);
                movieAdapter.notifyDataSetChanged();
            }
        }
    }
}
