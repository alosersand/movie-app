package it.alessandromarchi.moviest.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import it.alessandromarchi.moviest.database.MovieTableHelper;
import it.alessandromarchi.moviest.database.WishlistTableHelper;
import it.alessandromarchi.moviest.fragments.ConfirmDialogFragment;
import it.alessandromarchi.moviest.fragments.ConfirmDialogFragmentListener;

public class Wishlist extends AppCompatActivity implements ConfirmDialogFragmentListener {

	private MovieAdapter movieAdapter;
	private SQLiteDatabase database;
	private ListView moviesList;
	private MovieDB movieDB;

	private Cursor cursor;

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
				rimuoviFilm(id);

				return true;
			}
		});

		moviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				apriDettaglioFilm(id, view);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		caricaFilm();

		movieAdapter.notifyDataSetChanged();
	}

	private void caricaFilm() {
		database = movieDB.getReadableDatabase();
		cursor = database.query(WishlistTableHelper.TABLE_NAME, null, null, null, null, null, null);

		if (cursor != null) {
			movieAdapter.changeCursor(cursor);
			moviesList.setAdapter(movieAdapter);
		}
	}

	private void apriDettaglioFilm(long id, View view) {
		Intent movieDetail = new Intent(Wishlist.this, MovieDetail.class);
		String movieTitle;

		database = movieDB.getReadableDatabase();
		cursor = database.query(WishlistTableHelper.TABLE_NAME, new String[]{
				WishlistTableHelper._ID,
				WishlistTableHelper.TITLE
		}, WishlistTableHelper._ID + " = " + id, null, null, null, null);

		if (cursor != null) {
			cursor.moveToNext();
			movieTitle = cursor.getString(cursor.getColumnIndex(WishlistTableHelper.TITLE));
			movieDetail.putExtra("movie_title", movieTitle);
		}

		ActivityOptions options = ActivityOptions
				.makeSceneTransitionAnimation(Wishlist.this, view, "imageTransition");

		startActivity(movieDetail, options.toBundle());
	}

	private void rimuoviFilm(long id) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		ConfirmDialogFragment dialogFragment;

		database = movieDB.getReadableDatabase();
		cursor = database.query(WishlistTableHelper.TABLE_NAME, new String[]{
				WishlistTableHelper.TITLE,
				WishlistTableHelper._ID
		}, WishlistTableHelper._ID + " = " + id, null, null, null, null);

		if (cursor != null) {
			cursor.moveToNext();

			if (cursor.getCount() >= 1) {
				dialogFragment = new ConfirmDialogFragment(getString(R.string.remove_title), getString(R.string.dialog_remove_confirm, cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE))), id);
			} else {
				dialogFragment = new ConfirmDialogFragment(getString(R.string.remove_title), getString(R.string.dialog_remove_error_confirm), id);
			}

			dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
		}
	}

	@Override
	public void onPositivePressed(long movieID) {
		database = movieDB.getWritableDatabase();
		database.delete(WishlistTableHelper.TABLE_NAME, WishlistTableHelper._ID + " = " + movieID, null);

		caricaFilm();

		Toast.makeText(this, R.string.wishlist_remove, Toast.LENGTH_SHORT).show();
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
