package it.alessandromarchi.moviest.activities;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.List;
import java.util.Locale;

import it.alessandromarchi.moviest.R;
import it.alessandromarchi.moviest.adapters.MovieAdapter;
import it.alessandromarchi.moviest.database.MovieDB;
import it.alessandromarchi.moviest.database.MovieProvider;
import it.alessandromarchi.moviest.database.MovieTableHelper;
import it.alessandromarchi.moviest.database.WishlistTableHelper;
import it.alessandromarchi.moviest.fragments.ConfirmDialogFragment;
import it.alessandromarchi.moviest.fragments.ConfirmDialogFragmentListener;
import it.alessandromarchi.moviest.models.Movie;
import it.alessandromarchi.moviest.models.TMDBResponse;
import it.alessandromarchi.moviest.services.WebService;
import it.alessandromarchi.moviest.services.iWebServer;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ConfirmDialogFragmentListener {

	private static final int LOADER_ID = 568175;

	private boolean connected;

	public static Locale locale;

	private MovieDB movieDB;
	private SQLiteDatabase database;

	private MovieAdapter offlineSearch;
	private MovieAdapter movieAdapter;

	private Cursor cursor;

	private List<Movie> movies;

	private ProgressBar progressBar;
	private GridView moviesGrid;

	private WebService webService;

	iWebServer webServerListener = new iWebServer() {
		@Override
		public void onMoviesFetched(boolean success, TMDBResponse _TMDBResponse, int errorCode, String errorMessage) {
			if (success) {
				movies = _TMDBResponse.getMovies();
				int moviesSize = movies.size();

				ContentValues values = new ContentValues();

				cursor = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
						MovieTableHelper._ID,
						MovieTableHelper.TITLE
				}, null, null, null, null);

				if (cursor != null && cursor.getCount() >= 1) {
					for (int i = 0; i < moviesSize; i++) {
						values.put(MovieTableHelper.TITLE, movies.get(i).getTitle());
						values.put(MovieTableHelper.DESCRIPTION, movies.get(i).getDescription());
						values.put(MovieTableHelper.IMAGE_PATH, movies.get(i).getImagePath());
						values.put(MovieTableHelper.BACKGROUND_PATH, movies.get(i).getBackgroundPath());
						values.put(MovieTableHelper.RATING, movies.get(i).getRating());

						cursor.moveToPosition(i);

						if (cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)).equals(movies.get(i).getTitle())) {
							getContentResolver().update(Uri.parse(MovieProvider.MOVIES_URI + "/" + i), values, null, null);
						} else {
							getContentResolver().insert(MovieProvider.MOVIES_URI, values);
						}
					}

					cursor.close();

				} else {
					for (int i = 0; i < moviesSize; i++) {
						values.put(MovieTableHelper.TITLE, movies.get(i).getTitle());
						values.put(MovieTableHelper.DESCRIPTION, movies.get(i).getDescription());
						values.put(MovieTableHelper.IMAGE_PATH, movies.get(i).getImagePath());
						values.put(MovieTableHelper.BACKGROUND_PATH, movies.get(i).getBackgroundPath());
						values.put(MovieTableHelper.RATING, movies.get(i).getRating());

						getContentResolver().insert(MovieProvider.MOVIES_URI, values);
					}
				}

				movieAdapter.notifyDataSetChanged();

				progressBar.setVisibility(View.GONE);
				moviesGrid.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.GONE);

				moviesGrid.setAdapter(movieAdapter);

				Toast.makeText(MainActivity.this, R.string.offline_mode, Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu, menu);

		webService = WebService.getInstance();

		MenuItem actionWishlist = menu.findItem(R.id.action_wishlist);
		actionWishlist.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent wishlist = new Intent(MainActivity.this, Wishlist.class);
				startActivity(wishlist);

				return true;
			}
		});

		MenuItem actionSearch = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) actionSearch.getActionView();

		if (connected) {
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					getContentResolver().delete(MovieProvider.MOVIES_URI, null, null);

					if (newText.length() > 0) {
						webService.search(newText, true, webServerListener);
					} else {
						webService.getPopulars(webServerListener);
					}

					return true;
				}
			});
		} else {
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					if (newText.length() > 0) {
						cursor = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
								MovieTableHelper._ID,
								MovieTableHelper.TITLE,
								MovieTableHelper.DESCRIPTION,
								MovieTableHelper.IMAGE_PATH,
								MovieTableHelper.BACKGROUND_PATH,
								MovieTableHelper.IS_WISHLIST,
								MovieTableHelper.RATING
						}, MovieTableHelper.TITLE + " LIKE " + "'%" + newText + "%'", null, null, null);

						if (cursor != null && cursor.getCount() >= 1) {
							cursor.moveToNext();

							offlineSearch = new MovieAdapter(MainActivity.this, cursor);

							moviesGrid.setAdapter(offlineSearch);
						}
					} else {
						moviesGrid.setAdapter(movieAdapter);
					}

					return true;
				}
			});
		}

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkConnection();

		locale = getResources().getConfiguration().locale;

		webService = WebService.getInstance();

		movieDB = new MovieDB(this);
		movieAdapter = new MovieAdapter(this, null);

		progressBar = findViewById(R.id.progressBar);
		moviesGrid = findViewById(R.id.movies_grid);

		webService.getPopulars(webServerListener);

		moviesGrid.setAdapter(movieAdapter);
		moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				apriDettaglioFilm(id, view);
			}
		});
		moviesGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				addToWishlist(id);

				return true;
			}
		});

		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkConnection();

		moviesGrid.setVisibility(View.VISIBLE);
		movieAdapter.notifyDataSetChanged();
	}

	private void checkConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
				connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
	}

	private void apriDettaglioFilm(long id, View view) {
		Intent movieDetail = new Intent(MainActivity.this, MovieDetail.class);
		movieDetail.putExtra("movie_id", id);

		ActivityOptions options = ActivityOptions
				.makeSceneTransitionAnimation(MainActivity.this, view, "imageTransition");

		startActivity(movieDetail, options.toBundle());
	}

	private void addToWishlist(long id) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		ConfirmDialogFragment dialogFragment;

		database = movieDB.getReadableDatabase();
		Cursor titles = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
				MovieTableHelper.TITLE,
				MovieTableHelper._ID
		}, MovieTableHelper._ID + " = " + id, null, null, null);

		if (titles != null) {
			titles.moveToNext();

			String mainTitle = titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE));

			Cursor wishTitle = database.query(WishlistTableHelper.TABLE_NAME, new String[]{
					WishlistTableHelper.TITLE
			}, WishlistTableHelper.TITLE + " LIKE " + "'" + mainTitle + "'", null, null, null, null);

			if (wishTitle != null) {
				if (wishTitle.getCount() == 0) {
					dialogFragment = new ConfirmDialogFragment(
							getString(R.string.add_title),
							getString(R.string.dialog_add_confirm, titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE))),
							id);

					dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
				} else {
					Toast.makeText(MainActivity.this, R.string.already_isWishlist, Toast.LENGTH_SHORT).show();
				}

				wishTitle.close();
			} else {
				Toast.makeText(MainActivity.this, R.string.database_read_error, Toast.LENGTH_SHORT).show();
			}

			titles.close();
		}
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
		database = movieDB.getWritableDatabase();
		cursor = database.query(MovieTableHelper.TABLE_NAME, null, MovieTableHelper._ID + " = " + movieID, null, null, null, null);

		if (cursor != null) {
			cursor.moveToNext();

			ContentValues values = new ContentValues();
			values.put(WishlistTableHelper.TITLE, cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)));
			values.put(WishlistTableHelper.MOVIE_ID, cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
			values.put(WishlistTableHelper.DESCRIPTION, cursor.getString(cursor.getColumnIndex(MovieTableHelper.DESCRIPTION)));
			values.put(WishlistTableHelper.IMAGE_PATH, cursor.getString(cursor.getColumnIndex(MovieTableHelper.IMAGE_PATH)));
			values.put(WishlistTableHelper.RATING, cursor.getInt(cursor.getColumnIndex(MovieTableHelper.RATING)));

			database.insert(WishlistTableHelper.TABLE_NAME, null, values);
		}

		Toast.makeText(this, R.string.wishlist_add, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNegativePressed() {

	}

	@Override
	protected void onStop() {
		super.onStop();

		if (movieDB != null) movieDB.close();
		if (database != null) database.close();
		if (cursor != null) cursor.close();
	}
}
