package it.alessandromarchi.movieapp;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PicassoWrapper {


	public static void setImage(Context context, String imageUri, ImageView targetView) {
		ImageView view = targetView;

		if (view == null) {
			view = new ImageView(context);
		}


		Picasso.get()
				.load(imageUri)
				.placeholder(R.drawable.ic_movie)
				.error(R.drawable.ic_error)
				.into(view);
	}

}
