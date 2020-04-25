package it.alessandromarchi.movieapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;

import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.activities.MainActivity;
import it.alessandromarchi.movieapp.database.MovieTableHelper;

public class MovieAdapter extends CursorAdapter {

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
        int id = cursor.getInt(cursor.getColumnIndex(MovieTableHelper._ID));
        String title = cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE));
        String description = cursor.getString(cursor.getColumnIndex(MovieTableHelper.DESCRIPTION));
        String imagePath = cursor.getString(cursor.getColumnIndex(MovieTableHelper.IMAGE_PATH));
        int isWishlist = cursor.getInt(cursor.getColumnIndex(MovieTableHelper.IS_WISHLIST));
    }
}
