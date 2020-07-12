package it.alessandromarchi.moviest.activities;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import it.alessandromarchi.moviest.R;
import it.alessandromarchi.moviest.adapters.MovieAdapter;
import it.alessandromarchi.moviest.database.MovieDB;
import it.alessandromarchi.moviest.database.MovieProvider;
import it.alessandromarchi.moviest.database.MovieTableHelper;
import it.alessandromarchi.moviest.database.WishlistTableHelper;
import it.alessandromarchi.moviest.fragments.ConfirmDialogFragment;
import it.alessandromarchi.moviest.fragments.ConfirmDialogFragmentListener;

public class Wishlist extends AppCompatActivity implements /*LoaderManager.LoaderCallbacks<Cursor>,*/ ConfirmDialogFragmentListener {

//	private static final int LOADER_ID = 871462;

	SQLiteDatabase database;
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
		moviesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				ConfirmDialogFragment dialogFragment;

//				database = movieDB.getReadableDatabase();
//					Cursor titles = database.query(
//									WishlistTableHelper.TABLE_NAME,
//									new String[]{
////													WishlistTableHelper.TITLE,
//													WishlistTableHelper._ID
//									},
//									WishlistTableHelper.MOVIE_ID + " = " + id,
//									null,
//									null,
//									null,
//									null
//					);

				Cursor titles = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
						MovieTableHelper.TITLE,
						MovieTableHelper._ID
				}, MovieTableHelper._ID + " = " + id, null, null, null);


				if (titles != null) {
					titles.moveToNext();
					if (titles.getCount() >= 1) {
						dialogFragment = new ConfirmDialogFragment(getString(R.string.remove_title), getString(R.string.dialog_remove_confirm, titles.getString(titles.getColumnIndex(MovieTableHelper.TITLE))), id);
					} else {
						dialogFragment = new ConfirmDialogFragment(getString(R.string.remove_title), getString(R.string.dialog_remove_error_confirm), id);
					}

					titles.close();

					dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
				}


				return true;
			}
		});

		moviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent movieDetail = new Intent(Wishlist.this, MovieDetail.class);

				String movieTitle;

				database = movieDB.getReadableDatabase();
				Cursor film = database.query(WishlistTableHelper.TABLE_NAME, new String[]{
						WishlistTableHelper._ID,
						WishlistTableHelper.TITLE
				}, WishlistTableHelper._ID + " = " + id, null, null, null, null);

				if (film != null) {


					film.moveToNext();
//					Log.d("TAG", "onItemClick: " + film.getString(film.getColumnIndex(WishlistTableHelper.TITLE)));
					movieTitle = film.getString(film.getColumnIndex(WishlistTableHelper.TITLE));
//
//					Log.d("TAG", "onItemClick: " + movieID);
//
					movieDetail.putExtra("movie_title", movieTitle);
//
					film.close();
				}


				ActivityOptions options = ActivityOptions
						.makeSceneTransitionAnimation(Wishlist.this, view, "imageTransition");


				startActivity(movieDetail, options.toBundle());
			}
		});

		//TODO aggiornare con metodo non deprecato
//		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		caricaFilm();

		movieAdapter.notifyDataSetChanged();
	}

	private void caricaFilm() {
		database = movieDB.getReadableDatabase();

		Cursor films = database.query(WishlistTableHelper.TABLE_NAME, null, null, null, null, null, null);

		if (films != null) {
//			films.moveToNext();

			movieAdapter.changeCursor(films);
			moviesList.setAdapter(movieAdapter);
		}
	}


//	@NonNull
//	@Override
//	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
//		return new CursorLoader(
//				this,
//				MovieProvider.MOVIES_URI,
//				null,
//				MovieTableHelper.IS_WISHLIST + " = " + 1,
//				null,
//				null
//		);
//	}
//
//	@Override
//	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//		movieAdapter.changeCursor(data);
//	}
//
//	@Override
//	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//		movieAdapter.changeCursor(null);
//	}

	@Override
	public void onPositivePressed(long movieID) {
		ContentValues values = new ContentValues();
		values.put(MovieTableHelper.IS_WISHLIST, 0);

		getContentResolver().update(Uri.parse(MovieProvider.MOVIES_URI + "/" + movieID), values, null, null);

		Toast.makeText(this, R.string.wishlist_remove, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNegativePressed() {

	}
}
