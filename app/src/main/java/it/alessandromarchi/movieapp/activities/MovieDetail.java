package it.alessandromarchi.movieapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

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
				MovieTableHelper._ID
		}, MovieTableHelper._ID + " = " + id, null, null, null);


		if (movies != null && movies.getCount() != 0) {
			movies.moveToNext();
			if (movies.getCount() >= 1) {
				title.setText(movies.getString(movies.getColumnIndex(MovieTableHelper.TITLE)));
				description.setText(movies.getString(movies.getColumnIndex(MovieTableHelper.DESCRIPTION)));

				Picasso.get()
						.load(MovieAdapter.IMAGES_BASE_URL + movies.getString(movies.getColumnIndex(MovieTableHelper.BACKGROUND_PATH)))
						.placeholder(R.drawable.ic_movie)
						.error(R.drawable.ic_error)
						.into(detailIamge);

				setTitle(movies.getString(movies.getColumnIndex(MovieTableHelper.TITLE)));
			} else {
				title.setText(R.string.database_read_error);
				setTitle(R.string.app_name);
			}

			movies.close();
		} else {
			Toast.makeText(this, R.string.database_read_error, Toast.LENGTH_SHORT).show();
		}



		database.close();
	}
}
