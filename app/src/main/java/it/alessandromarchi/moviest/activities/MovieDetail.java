package it.alessandromarchi.moviest.activities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

public class MovieDetail extends AppCompatActivity {
	TextView ratingText;
	TextView description;
	ImageView detailImage;
	//	ViewPager2 pager;
	RatingBar ratingBar;

	SQLiteDatabase database;
	MovieDB movieDB;


	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		ratingText = findViewById(R.id.rating_text);
		description = findViewById(R.id.detail_description);
		ratingBar = findViewById(R.id.rating_bar);
		detailImage = findViewById(R.id.detail_image);
//		pager = findViewById(R.id.pager);

		movieDB = new MovieDB(this);
		database = movieDB.getReadableDatabase();

		Intent intent = getIntent();
		long id = intent.getLongExtra("movie_id", 0);

		Cursor movies = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
				MovieTableHelper.IMAGE_PATH,
				MovieTableHelper.TITLE,
				MovieTableHelper.DESCRIPTION,
				MovieTableHelper.RATING,
				MovieTableHelper._ID
		}, MovieTableHelper._ID + " = " + id, null, null, null);


		if (movies != null && movies.getCount() >= 1) {
			movies.moveToNext();

			float rawStars = movies.getFloat(movies.getColumnIndex(MovieTableHelper.RATING));
			float stars = rawStars / 2;

			ObjectAnimator anim = ObjectAnimator.ofFloat(ratingBar, "rating", stars);
			anim.setDuration(750);
			anim.start();

//			ratingBar.setRating(stars);
			ratingText.setText(rawStars + "/10");
//			ratingText.setCompoundDrawablesWithIntrinsicBounds(
//					0, 0, R.drawable.ic_star, 0);

			description.setText(movies.getString(movies.getColumnIndex(MovieTableHelper.DESCRIPTION)) + "\n");
			description.setMovementMethod(new ScrollingMovementMethod());

//			Picasso.get()
//					.load(MovieAdapter.IMAGES_BASE_URL + movies.getString(movies.getColumnIndex(MovieTableHelper.BACKGROUND_PATH)))
//					.placeholder(R.drawable.ic_movie)
//					.error(R.drawable.ic_error)
//					.into(detailIamge);


			GlideWrapper.setImage(this, MovieAdapter.IMAGES_BASE_URL + movies.getString(movies.getColumnIndex(MovieTableHelper.IMAGE_PATH)), detailImage);
//			GlideWrapper.setImage(this, MovieAdapter.IMAGES_BASE_URL + movies.getString(movies.getColumnIndex(MovieTableHelper.BACKGROUND_PATH)), detailImage2);

//			pager.addView(detailImage);


			setTitle(movies.getString(movies.getColumnIndex(MovieTableHelper.TITLE)));


			movies.close();
		} else {
			setTitle(R.string.app_name);
			ratingText.setText("");
			Toast.makeText(this, R.string.database_read_error, Toast.LENGTH_SHORT).show();
		}


		database.close();
	}
}
