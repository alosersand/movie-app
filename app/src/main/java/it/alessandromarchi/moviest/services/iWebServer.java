package it.alessandromarchi.moviest.services;

import it.alessandromarchi.moviest.models.TMDBResponse;

public interface iWebServer {
	void onMoviesFetched(boolean success, TMDBResponse _TMDBResponse, int errorCode, String errorMessage);
}
