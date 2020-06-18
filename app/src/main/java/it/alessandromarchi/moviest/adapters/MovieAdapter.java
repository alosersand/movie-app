package it.alessandromarchi.moviest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import it.alessandromarchi.moviest.GlideWrapper;
import it.alessandromarchi.moviest.R;
import it.alessandromarchi.moviest.activities.MainActivity;
import it.alessandromarchi.moviest.activities.Wishlist;
import it.alessandromarchi.moviest.database.MovieTableHelper;

public class MovieAdapter extends CursorAdapter {

	public static String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/original";

	//TODO aggiornare con metodo non deprecato
	public MovieAdapter(Context context, Cursor c) {
		super(context, c);
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);

		View movie;

		if (context instanceof MainActivity) {
			movie = layoutInflater.inflate(R.layout.movie_item, null);
		} else {
			movie = layoutInflater.inflate(R.layout.movie_row, null);
		}

		return movie;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
//        int id = cursor.getInt(cursor.getColumnIndex(MovieTableHelper._ID));
		String title = cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE));
		// String description = cursor.getString(cursor.getColumnIndex(MovieTableHelper.DESCRIPTION));
		String imagePath = cursor.getString(cursor.getColumnIndex(MovieTableHelper.IMAGE_PATH));
		int isWishlist = cursor.getInt(cursor.getColumnIndex(MovieTableHelper.IS_WISHLIST));

		ImageView imageView;

		if (context instanceof MainActivity) {
			imageView = view.findViewById(R.id.grid_item);

//			Picasso.get()
//					.load(IMAGES_BASE_URL + imagePath)
//					.placeholder(R.drawable.ic_movie)
//					.error(R.drawable.ic_error)
//					.into(imageView);

			GlideWrapper.setImage(context, IMAGES_BASE_URL + imagePath, imageView);

			if (isWishlist == 1) {
				view.findViewById(R.id.grid_item_star).setVisibility(View.VISIBLE);
			} else {
				view.findViewById(R.id.grid_item_star).setVisibility(View.INVISIBLE);
			}
		} else if (context instanceof Wishlist) {
			TextView rowTitle = view.findViewById(R.id.row_title);
			rowTitle.setText(title);

			imageView = view.findViewById(R.id.row_image);
//			Picasso.get()
//					.load(IMAGES_BASE_URL + imagePath)
//					.placeholder(R.drawable.ic_movie)
//					.error(R.drawable.ic_error)
//					.into(imageView);

			GlideWrapper.setImage(context, IMAGES_BASE_URL + imagePath, imageView);
		}


	}
}
