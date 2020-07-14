package it.alessandromarchi.moviest;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideWrapper {
	public static void setImage(Context context, String imageUri, ImageView targetView) {
		Glide.with(context).load(imageUri).fitCenter().placeholder(R.drawable.ic_movie).into(targetView);
	}
}
