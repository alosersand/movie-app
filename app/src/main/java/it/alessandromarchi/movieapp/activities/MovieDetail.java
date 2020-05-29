package it.alessandromarchi.movieapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it.alessandromarchi.movieapp.PicassoWrapper;
import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.adapters.MovieAdapter;
import it.alessandromarchi.movieapp.database.MovieDB;
import it.alessandromarchi.movieapp.database.MovieProvider;
import it.alessandromarchi.movieapp.database.MovieTableHelper;

public class MovieDetail extends AppCompatActivity {
	TextView title;
	TextView description;
	ImageView detailIamge;

	SQLiteDatabase database;
	MovieDB movieDB;


	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		title = findViewById(R.id.detail_title);
		description = findViewById(R.id.detail_description);
		detailIamge = findViewById(R.id.detail_image);

		movieDB = new MovieDB(this);
		database = movieDB.getReadableDatabase();

		Intent intent = getIntent();
		long id = intent.getLongExtra("movie_id", 0);

		Cursor movies = getContentResolver().query(Uri.parse("" + MovieProvider.MOVIES_URI), new String[]{
				MovieTableHelper.BACKGROUND_PATH,
				MovieTableHelper.DESCRIPTION,
				MovieTableHelper.TITLE,
				MovieTableHelper.IS_WISHLIST,
				MovieTableHelper._ID
		}, MovieTableHelper._ID + " = " + id, null, null, null);


		if (movies != null && movies.getCount() >= 1) {
			movies.moveToNext();


			if (movies.getInt(movies.getColumnIndex(MovieTableHelper.IS_WISHLIST)) == 1) {
				title.setCompoundDrawablesWithIntrinsicBounds(
						0, 0, R.drawable.ic_star, 0);

			}

			title.setText(movies.getString(movies.getColumnIndex(MovieTableHelper.TITLE)));

			description.setText(movies.getString(movies.getColumnIndex(MovieTableHelper.DESCRIPTION)) + "\n");
			description.setMovementMethod(new ScrollingMovementMethod());

//			Picasso.get()
//					.load(MovieAdapter.IMAGES_BASE_URL + movies.getString(movies.getColumnIndex(MovieTableHelper.BACKGROUND_PATH)))
//					.placeholder(R.drawable.ic_movie)
//					.error(R.drawable.ic_error)
//					.into(detailIamge);

			PicassoWrapper.setImage(this, MovieAdapter.IMAGES_BASE_URL + movies.getString(movies.getColumnIndex(MovieTableHelper.BACKGROUND_PATH)), detailIamge);


			setTitle(movies.getString(movies.getColumnIndex(MovieTableHelper.TITLE)));


			movies.close();
		} else {
			setTitle(R.string.app_name);
			title.setText("");
			Toast.makeText(this, R.string.database_read_error, Toast.LENGTH_SHORT).show();
		}


		database.close();
	}
}
