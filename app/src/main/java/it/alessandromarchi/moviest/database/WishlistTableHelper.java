package it.alessandromarchi.moviest.database;

import android.provider.BaseColumns;

public class WishlistTableHelper implements BaseColumns {

	public static final String TABLE_NAME = "wishlist";
	public static final String MOVIE_ID = "movie_id";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String IMAGE_PATH = "image_path";
	public static final String RATING = "rating";

	public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
			_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			MOVIE_ID + " INTEGER, " +
			TITLE + " TEXT, " +
			DESCRIPTION + " TEXT, " +
			IMAGE_PATH + " TEXT, " +
			RATING + " FLOAT);";
}
