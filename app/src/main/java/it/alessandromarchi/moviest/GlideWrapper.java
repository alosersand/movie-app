package it.alessandromarchi.moviest;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideWrapper {


//	public static void setImage(Context context, String imageUri, ImageView targetView) {
//		ImageView view = targetView;
//
//		if (view == null) {
//			view = new ImageView(context);
//		}
//
//
////		Picasso.get()
////				.load(imageUri)
////				.placeholder(R.drawable.ic_movie)
////				.error(R.drawable.ic_error)
////				.into(view);
//
//		Picasso.get()
//				.load(imageUri)
//				.error(R.drawable.ic_error)
//				.into(view);
//
//
//	}

	public static void setImage(Context context, String imageUri, ImageView targetView) {
		Glide.with(context).load(imageUri).fitCenter().placeholder(R.drawable.ic_movie).into(targetView);
	}

}
