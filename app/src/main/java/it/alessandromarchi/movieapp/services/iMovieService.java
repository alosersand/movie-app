package it.alessandromarchi.movieapp.services;

import it.alessandromarchi.movieapp.models.TMDBResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface iMovieService {

	@GET("/{version}/movie/{category}")
	Call<TMDBResponse> getResponse(
					@Path("version") String version,
					@Path("category") String category,
					@Query("api_key") String apiKey,
					@Query("language") String language,
					@Query("page") String page,
					@Query("region") String region
	);
}
