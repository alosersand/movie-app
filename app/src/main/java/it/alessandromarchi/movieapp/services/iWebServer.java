package it.alessandromarchi.movieapp.services;

import it.alessandromarchi.movieapp.models.TMDBResponse;

public interface iWebServer {
	//    void onMoviesFetched(boolean success, List<Movie> movies, int errorCode, String errorMessage);
	void onMoviesFetched(boolean success, TMDBResponse _TMDBResponse, int errorCode, String errorMessage);
}
