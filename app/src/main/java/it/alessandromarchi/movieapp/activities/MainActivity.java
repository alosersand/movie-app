package it.alessandromarchi.movieapp.activities;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import java.util.Locale;

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

	public static Locale locale;

	List<Movie> movies;

	MovieDB movieDB;
	MovieAdapter movieAdapter;

	GridView moviesGrid;
	MenuItem actionWishlist;
	MenuItem actionSearch;
	ProgressBar progressBar;

	WebService webService;
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

		// TMP
		actionSearch = menu.getItem(1);
		actionSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.d("TAG", "DELETE ALL");
				getContentResolver().delete(MovieProvider.MOVIES_URI, null, null);

				return true;
			}
		});

		return true;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


//		getWindow().setExitTransition(new Explode());

		locale = getResources().getConfiguration().locale;

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

				Cursor titles = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
						MovieTableHelper.IS_WISHLIST,
						MovieTableHelper.TITLE,
						MovieTableHelper._ID
				}, MovieTableHelper._ID + " = " + id, null, null, null);

				// RISCRIVERE
//				if (titles != null && titles.getCount() >= 1) {
//					titles.moveToNext();
//
//					if (titles.getInt(titles.getColumnIndex(MovieTableHelper.IS_WISHLIST)) == 0) {
//
//						if (titles.getCount() >= 1) {
//
//
//							dialogFragment = new ConfirmDialogFragment(
//									getString(R.string.add_title),
//									getString(R.string.dialog_add_confirm, titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE))),
//									id);
//						} else {
//							dialogFragment = new ConfirmDialogFragment(getString(R.string.add_title), getString(R.string.dialog_add_error_confirm), id);
//						}
//
//						dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
//					}
//					titles.close();
//				} else {
//					Toast.makeText(MainActivity.this, R.string.database_read_error, Toast.LENGTH_SHORT).show();
//				}
				// ----


				if (titles != null && titles.getCount() >= 1) {
					titles.moveToNext();

					if (titles.getInt(titles.getColumnIndex(MovieTableHelper.IS_WISHLIST)) == 0) {
						dialogFragment = new ConfirmDialogFragment(
								getString(R.string.add_title),
								getString(R.string.dialog_add_confirm, titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE))),
								id);

						dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
					} else {
						Toast.makeText(MainActivity.this, R.string.already_isWishlist, Toast.LENGTH_SHORT).show();
					}

					titles.close();
				} else {
					Toast.makeText(MainActivity.this, R.string.database_read_error, Toast.LENGTH_SHORT).show();
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

		getContentResolver().update(Uri.parse(MovieProvider.MOVIES_URI + "/" + movieID), values, null, null);

		Toast.makeText(this, R.string.wishlist_add, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNegativePressed() {

	}
}
