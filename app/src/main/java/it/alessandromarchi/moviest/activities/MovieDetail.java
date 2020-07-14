package it.alessandromarchi.moviest.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it.alessandromarchi.moviest.GlideWrapper;
import it.alessandromarchi.moviest.R;
import it.alessandromarchi.moviest.adapters.MovieAdapter;
import it.alessandromarchi.moviest.database.MovieDB;
import it.alessandromarchi.moviest.database.MovieProvider;
import it.alessandromarchi.moviest.database.MovieTableHelper;
import it.alessandromarchi.moviest.database.WishlistTableHelper;

public class MovieDetail extends AppCompatActivity {
	private ImageView detailImage;
	private TextView description;
	private TextView ratingText;

	private String movieTitle;

	private RatingBar ratingBar;

	private SQLiteDatabase database;
	private MovieDB movieDB;

	private Cursor cursor;

	private long id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		ratingText = findViewById(R.id.rating_text);
		description = findViewById(R.id.detail_description);
		ratingBar = findViewById(R.id.rating_bar);
		detailImage = findViewById(R.id.detail_image);

		movieDB = new MovieDB(this);
		database = movieDB.getReadableDatabase();

		Intent intent = getIntent();
		id = intent.getLongExtra("movie_id", 0);
		movieTitle = intent.getStringExtra("movie_title");

		fecthFilms();
		aggiornaUI();
	}

	private void fecthFilms() {
		if (movieTitle == null) {
			cursor = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
					MovieTableHelper.IMAGE_PATH,
					MovieTableHelper.TITLE,
					MovieTableHelper.DESCRIPTION,
					MovieTableHelper.RATING,
					MovieTableHelper._ID
			}, MovieTableHelper._ID + " = " + id, null, null, null);
		} else {
			database = movieDB.getReadableDatabase();
			cursor = database.query(WishlistTableHelper.TABLE_NAME, new String[]{
					WishlistTableHelper.IMAGE_PATH,
					WishlistTableHelper.TITLE,
					WishlistTableHelper.DESCRIPTION,
					WishlistTableHelper.RATING,
					WishlistTableHelper._ID
			}, WishlistTableHelper.TITLE + " LIKE " + "'" + movieTitle + "'", null, null, null, null);
		}
	}

	private void aggiornaUI() {
		if (cursor != null && cursor.getCount() >= 1) {
			cursor.moveToNext();

			float rawStars = cursor.getFloat(cursor.getColumnIndex(MovieTableHelper.RATING));
			float stars = rawStars / 2;

			ObjectAnimator anim = ObjectAnimator.ofFloat(ratingBar, "rating", stars);
			anim.setDuration(750);
			anim.start();

			ratingText.setText(getString(R.string.rating_stars, rawStars));

			description.setText(cursor.getString(cursor.getColumnIndex(MovieTableHelper.DESCRIPTION)));
			description.setMovementMethod(new ScrollingMovementMethod());

			GlideWrapper.setImage(this, MovieAdapter.IMAGES_BASE_URL + cursor.getString(cursor.getColumnIndex(MovieTableHelper.IMAGE_PATH)), detailImage);

			setTitle(cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)));
		} else {
			setTitle(R.string.app_name);

			ratingText.setText("");

			Toast.makeText(this, R.string.database_read_error, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (movieDB != null) movieDB.close();
		if (database != null) database.close();
		if (cursor != null) cursor.close();
	}
}