package it.alessandromarchi.movieapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import it.alessandromarchi.movieapp.R;
import it.alessandromarchi.movieapp.database.MovieTableHelper;

public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View movieItem = layoutInflater.inflate(R.layout.movie_item, null);

        return movieItem;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE));
        String description = cursor.getString(cursor.getColumnIndex(MovieTableHelper.DESCRIPTION));
        String imagePath = cursor.getString(cursor.getColumnIndex(MovieTableHelper.IMAGE_PATH));
        boolean isSeen = cursor.getInt(cursor.getColumnIndex(MovieTableHelper.IS_SEEN)) == 1;
        int id = cursor.getInt(cursor.getColumnIndex(MovieTableHelper._ID));

        TextView movieTitle = view.findViewById(R.id.movie_title);

        movieTitle.setText(title);
    }
}
