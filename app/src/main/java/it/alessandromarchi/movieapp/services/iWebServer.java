package it.alessandromarchi.movieapp.services;

import it.alessandromarchi.movieapp.models.Result;

public interface iWebServer {
	//    void onMoviesFetched(boolean success, List<Movie> movies, int errorCode, String errorMessage);
	void onMoviesFetched(boolean success, Result result, int errorCode, String errorMessage);
}
