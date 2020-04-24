package it.alessandromarchi.movieapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.alessandromarchi.movieapp.R;

public class Wishlist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        setTitle("Wishlist");
    }
}
