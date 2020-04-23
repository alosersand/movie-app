package it.alessandromarchi.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import it.alessandromarchi.movieapp.R;

public class MovieDetail extends AppCompatActivity {

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        title = findViewById(R.id.title);

        Intent intent = getIntent();
        Long id = intent.getLongExtra("movie_id", 0);

        title.setText(id.toString());
    }
}
