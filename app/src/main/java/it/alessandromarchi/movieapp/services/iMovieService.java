package it.alessandromarchi.movieapp.services;

import java.util.List;

import it.alessandromarchi.movieapp.models.Movie;
import it.alessandromarchi.movieapp.models.Result;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface iMovieService {

	@GET("movie/popular")
	Call<Result> getResult(
					@Query("api_key") String apiKey,
					@Query("language") String language,
					@Query("page") String page,
					@Query("region") String region
	);

	List<Movie> getMovies();
}
