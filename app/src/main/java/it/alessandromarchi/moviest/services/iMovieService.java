package it.alessandromarchi.moviest.services;

import it.alessandromarchi.moviest.models.TMDBResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface iMovieService {
	@GET("/{version}/movie/{category}")
	Call<TMDBResponse> getPopularsMovies(
			@Path("version") int version,
			@Path("category") String category,
			@Query("api_key") String apiKey,
			@Query("language") String language,
			@Query("page") int page,
			@Query("region") String region
	);

	@GET("/{version}/search/movie")
	Call<TMDBResponse> searchMovies(
			@Path("version") int version,
			@Query("api_key") String apiKey,
			@Query("language") String language,
			@Query("query") String query,
			@Query("page") int page,
			@Query("include_adult") boolean includeAdult,
			@Query("region") String region
	);
}
