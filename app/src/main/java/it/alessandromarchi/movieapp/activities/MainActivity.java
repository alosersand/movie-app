package it.alessandromarchi.movieapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.List;

import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.adapters.MovieAdapter;
import it.alessandromarchi.movieapp.database.MovieDB;
import it.alessandromarchi.movieapp.database.MovieProvider;
import it.alessandromarchi.movieapp.database.MovieTableHelper;
import it.alessandromarchi.movieapp.fragments.ConfirmDialogFragment;
import it.alessandromarchi.movieapp.fragments.ConfirmDialogFragmentListener;
import it.alessandromarchi.movieapp.models.Movie;
import it.alessandromarchi.movieapp.models.TMDBResponse;
import it.alessandromarchi.movieapp.services.WebService;
import it.alessandromarchi.movieapp.services.iWebServer;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ConfirmDialogFragmentListener {

	private static final int LOADER_ID = 568175;

	List<Movie> movies;

	SQLiteDatabase database;
	MovieDB movieDB;
	MovieAdapter movieAdapter;

	GridView moviesGrid;
	MenuItem actionWishlist;
	ProgressBar progressBar;

	WebService webService;
	iWebServer webServerListener = new iWebServer() {
		@Override
		public void onMoviesFetched(boolean success, TMDBResponse _TMDBResponse, int errorCode, String errorMessage) {
			if (success) {
				movies = _TMDBResponse.getMovies();

				Log.d("boi", "onMoviesFetched: " + movies);

				ContentValues values = new ContentValues();
				for (Movie movie : movies) {
					values.put(MovieTableHelper.TITLE, movie.getTitle());
					values.put(MovieTableHelper.DESCRIPTION, movie.getDescription());
				}
				getContentResolver().insert(MovieProvider.MOVIES_URI, values);

				movieAdapter.notifyDataSetChanged();

				progressBar.setVisibility(View.GONE);
				moviesGrid.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.VISIBLE);
				moviesGrid.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu, menu);

		actionWishlist = menu.getItem(0);
		actionWishlist.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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

		webService = WebService.getInstance();

		movieDB = new MovieDB(this);
		movieAdapter = new MovieAdapter(this, null);

		progressBar = findViewById(R.id.progressBar);

		moviesGrid = findViewById(R.id.movies_grid);

		webService.getMovies(webServerListener);

		moviesGrid.setAdapter(movieAdapter);
		moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent movieDetail = new Intent(MainActivity.this, MovieDetail.class);
				movieDetail.putExtra("movie_id", id);
				startActivity(movieDetail);
			}
		});
		moviesGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				ConfirmDialogFragment dialogFragment;

				database = movieDB.getReadableDatabase();
				Cursor titles = database.query(
								MovieTableHelper.TABLE_NAME,
								new String[]{
												MovieTableHelper.TITLE,
												MovieTableHelper._ID
								},
								MovieTableHelper._ID + " = " + id,
								null,
								null,
								null,
								null
				);

				titles.moveToNext();
				if (titles.getCount() >= 1) {
					dialogFragment = new ConfirmDialogFragment(getString(R.string.add_title), getString(R.string.dialog_add_confirm, titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE))), id);
				} else {
					dialogFragment = new ConfirmDialogFragment(getString(R.string.add_title), getString(R.string.dialog_add_error_confirm), id);
				}
				titles.close();

				dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());

				return true;
			}
		});

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

	@Override
	public void onPositivePressed(long movieID) {
		ContentValues values = new ContentValues();
		values.put(MovieTableHelper.IS_WISHLIST, 1);

		int uri = getContentResolver().update(Uri.parse(MovieProvider.MOVIES_URI + "/" + movieID), values, null, null);

		Toast.makeText(this, R.string.wishlist_add, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNegativePressed() {

	}
}
