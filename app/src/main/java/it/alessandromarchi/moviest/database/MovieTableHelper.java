package it.alessandromarchi.moviest.database;

import android.provider.BaseColumns;

public class MovieTableHelper implements BaseColumns {

	public static final String TABLE_NAME = "movies";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String IMAGE_PATH = "image_path";
	public static final String BACKGROUND_PATH = "background_path";
	public static final String IS_WISHLIST = "is_wishlist";
	public static final String RATING = "rating";

	public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
			_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			TITLE + " TEXT, " +
			DESCRIPTION + " TEXT, " +
			IMAGE_PATH + " TEXT, " +
			BACKGROUND_PATH + " TEXT, " +
			RATING + " FLOAT, " +
			IS_WISHLIST + " INTEGER DEFAULT 0);";
}
