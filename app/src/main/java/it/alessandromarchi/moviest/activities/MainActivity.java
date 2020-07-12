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

	public static final String TAG = "paradiddle";
	private static final int LOADER_ID = 568175;

	public static Locale locale;

	View movie;

	List<Movie> movies;
	MovieAdapter offlineSearch;

	MovieDB movieDB;
	SQLiteDatabase database;
	MovieAdapter movieAdapter;

	iWebServer webServerListener = new iWebServer() {
		@Override
		public void onMoviesFetched(boolean success, TMDBResponse _TMDBResponse, int errorCode, String errorMessage) {
			if (success) {
				movies = _TMDBResponse.getMovies();
				int moviesSize = movies.size();

				ContentValues values = new ContentValues();

				Cursor titles = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
						MovieTableHelper._ID,
						MovieTableHelper.TITLE
				}, null, null, null, null);

				if (titles != null && titles.getCount() >= 1) {
					for (int i = 0; i < moviesSize; i++) {
						values.put(MovieTableHelper.TITLE, movies.get(i).getTitle());
						values.put(MovieTableHelper.DESCRIPTION, movies.get(i).getDescription());
						values.put(MovieTableHelper.IMAGE_PATH, movies.get(i).getImagePath());
						values.put(MovieTableHelper.BACKGROUND_PATH, movies.get(i).getBackgroundPath());
						values.put(MovieTableHelper.RATING, movies.get(i).getRating());

						titles.moveToPosition(i);

//						Log.d("TAG", "TITLES(" + i + "): " + titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE)));
//						Log.d("TAG", "MOVIES(" + i + "): " + movies.get(i).getTitle());

						if (titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE)).equals(movies.get(i).getTitle())) {
//							Log.d("TAG", "UPDATE");
							getContentResolver().update(Uri.parse(MovieProvider.MOVIES_URI + "/" + i), values, null, null);
						} else {
//							Log.d("TAG", "INSERT");
							getContentResolver().insert(MovieProvider.MOVIES_URI, values);
						}
					}

					titles.close();

				} else {
					Log.d("TAG", "NEW INSERT");

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

//				Toast.makeText(MainActivity.this, R.string.online_mode, Toast.LENGTH_LONG).show();
			} else {
				progressBar.setVisibility(View.GONE);
//				moviesGrid.setVisibility(View.GONE);

				moviesGrid.setAdapter(movieAdapter);

				Toast.makeText(MainActivity.this, R.string.offline_mode, Toast.LENGTH_LONG).show();
			}
		}
	};

	GridView moviesGrid;
	GridView moviesGridSearch;
	ProgressBar progressBar;

	MenuItem actionWishlist;
	MenuItem actionSearch;

	SearchView searchView;

	WebService webService;
	private boolean connected;

//

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu, menu);
		Log.d(TAG, "onCreateOptionsMenu: ");

		webService = WebService.getInstance();

//		movieAdapterSearch = new MovieAdapter(this, null);

		actionWishlist = menu.findItem(R.id.action_wishlist);
		actionWishlist.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {

				Intent wishlist = new Intent(MainActivity.this, Wishlist.class);
				startActivity(wishlist);

				return true;
			}
		});

		actionSearch = menu.findItem(R.id.action_search);
		searchView = (SearchView) actionSearch.getActionView();

		if (connected) {
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {

//					moviesGrid.setVisibility(View.GONE);
//					moviesGridSearch.setVisibility(View.VISIBLE);
//
//
					getContentResolver().delete(MovieProvider.MOVIES_URI, null, null);
//
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


						Cursor cFilms = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
								MovieTableHelper._ID,
								MovieTableHelper.TITLE,
								MovieTableHelper.DESCRIPTION,
								MovieTableHelper.IMAGE_PATH,
								MovieTableHelper.BACKGROUND_PATH,
								MovieTableHelper.IS_WISHLIST,
								MovieTableHelper.RATING
						}, MovieTableHelper.TITLE + " LIKE " + "'%" + newText + "%'", null, null, null);


						if (cFilms != null && cFilms.getCount() >= 1) {
							cFilms.moveToNext();

//						for (int i = 0; i < cFilms.getCount(); i++) {
//							cFilms.moveToPosition(i);

							offlineSearch = new MovieAdapter(MainActivity.this, cFilms);
//							movieAdapter.changeCursor(cFilms);

//							Movie movie = new Movie(
//									cFilms.getString(cFilms.getColumnIndex(MovieTableHelper.TITLE)),
//									cFilms.getString(cFilms.getColumnIndex(MovieTableHelper.DESCRIPTION)),
//									cFilms.getString(cFilms.getColumnIndex(MovieTableHelper.IMAGE_PATH)),
//									cFilms.getString(cFilms.getColumnIndex(MovieTableHelper.BACKGROUND_PATH)),
//									cFilms.getInt(cFilms.getColumnIndex(MovieTableHelper.IS_WISHLIST)),
//									cFilms.getFloat(cFilms.getColumnIndex(MovieTableHelper.RATING))
//							);


//							moviesGrid.removeAllViewsInLayout();


//							View movie = movieAdapterSearch.newView(MainActivity.this, cFilms, null);
//							movieAdapterSearch.bindView(movie, MainActivity.this, cFilms);
//							Log.d(TAG, "onQueryTextChange: " + movie);

							moviesGrid.setAdapter(offlineSearch);
//							movieAdapterSearch.notifyDataSetChanged();

//						}

//
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
//		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		Log.d(TAG, "onCreate: ");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
				connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;


//		getWindow().setExitTransition(new Explode());

		locale = getResources().getConfiguration().locale;

		webService = WebService.getInstance();

		movieDB = new MovieDB(this);
		movieAdapter = new MovieAdapter(this, null);

		progressBar = findViewById(R.id.progressBar);
		moviesGrid = findViewById(R.id.movies_grid);
		moviesGridSearch = findViewById(R.id.movies_grid_search);

		webService.getPopulars(webServerListener);

		moviesGrid.setAdapter(movieAdapter);
		moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent movieDetail = new Intent(MainActivity.this, MovieDetail.class);
				movieDetail.putExtra("movie_id", id);

				Log.d("TAG", "onItemClick: " + id);

//				final View star = view.findViewById(R.id.grid_item_star);
//
//				Pair[] pairs = new Pair[2];
//				pairs[0] = new Pair<View, String>(view, "imageTransition");
//				pairs[1] = new Pair<View, String>(star, "starTransition");

				ActivityOptions options = ActivityOptions
						.makeSceneTransitionAnimation(MainActivity.this, view, "imageTransition");

//				ActivityOptions options = ActivityOptions
//						.makeSceneTransitionAnimation(MainActivity.this, pairs);


				startActivity(movieDetail, options.toBundle());
			}
		});
		moviesGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				ConfirmDialogFragment dialogFragment;

				database = movieDB.getReadableDatabase();

				Cursor titles = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
//						MovieTableHelper.IS_WISHLIST,
						MovieTableHelper.TITLE,
						MovieTableHelper._ID
				}, MovieTableHelper._ID + " = " + id, null, null, null);

				// RISCRIOVERE CONTROLLO TITOLO WISH
//				if (titles != null && titles.getCount() >= 1) {
//					titles.moveToNext();
//
//					if (titles.getInt(titles.getColumnIndex(MovieTableHelper.IS_WISHLIST)) == 0) {
//						dialogFragment = new ConfirmDialogFragment(
//								getString(R.string.add_title),
//								getString(R.string.dialog_add_confirm, titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE))),
//								id);
//
//						dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
//					} else {
//						Toast.makeText(MainActivity.this, R.string.already_isWishlist, Toast.LENGTH_SHORT).show();
//					}
//
//					titles.close();
//				} else {
//					Toast.makeText(MainActivity.this, R.string.database_read_error, Toast.LENGTH_SHORT).show();
//				}

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


				return true;
			}
		});

		//TODO aggiornare con metodo non deprecato
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();


		moviesGrid.setVisibility(View.VISIBLE);
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

//		ContentValues values = new ContentValues();
//		values.put(MovieTableHelper.IS_WISHLIST, 1);
//
//		getContentResolver().update(Uri.parse(MovieProvider.MOVIES_URI + "/" + movieID), values, null, null);


		// 2 tabelle
		database = movieDB.getWritableDatabase();
		Cursor film = database.query(MovieTableHelper.TABLE_NAME, null, MovieTableHelper._ID + " = " + movieID, null, null, null, null);

		if (film != null) {
			film.moveToNext();

			ContentValues values = new ContentValues();
			values.put(WishlistTableHelper.TITLE, film.getString(film.getColumnIndex(MovieTableHelper.TITLE)));
			values.put(WishlistTableHelper.MOVIE_ID, film.getLong(film.getColumnIndex(MovieTableHelper._ID)));
			values.put(WishlistTableHelper.DESCRIPTION, film.getString(film.getColumnIndex(MovieTableHelper.DESCRIPTION)));
			values.put(WishlistTableHelper.IMAGE_PATH, film.getString(film.getColumnIndex(MovieTableHelper.IMAGE_PATH)));
			values.put(WishlistTableHelper.RATING, film.getInt(film.getColumnIndex(MovieTableHelper.RATING)));

			database.insert(WishlistTableHelper.TABLE_NAME, null, values);

			film.close();
		}

		Toast.makeText(this, R.string.wishlist_add, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onNegativePressed() {

	}
}
