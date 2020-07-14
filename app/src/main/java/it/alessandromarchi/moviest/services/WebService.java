package it.alessandromarchi.moviest.services;

import java.io.IOException;

import it.alessandromarchi.moviest.models.TMDBResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

	private iMovieService iMovieService;

	private static WebService instance;

	private String MOVIES_BASE_URL = "https://api.themoviedb.org/";
	private String API_KEY = "5771171ef3fdb433ef45cd105f4b541f";
	private String CATEGORY = "popular";
	private String LANGUAGE = "it-IT";
	private String REGION = "IT";

	private int VERSION = 3;
	private int PAGE = 1;

	private WebService() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(MOVIES_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		iMovieService = retrofit.create(iMovieService.class);
	}

	public static WebService getInstance() {
		if (instance == null)
			instance = new WebService();
		return instance;
	}

	public void getPopulars(final iWebServer listener) {
		Call<TMDBResponse> response = iMovieService.getPopularsMovies(VERSION, CATEGORY, API_KEY, LANGUAGE, PAGE, REGION);

		response.enqueue(new Callback<TMDBResponse>() {
			@Override
			public void onResponse(Call<TMDBResponse> call, Response<TMDBResponse> response) {
				if (response.code() == 200) {
					listener.onMoviesFetched(true, response.body(), -1, null);
				} else {
					try {
						listener.onMoviesFetched(true, null, response.code(), response.errorBody().string());
					} catch (IOException e) {
						listener.onMoviesFetched(true, null, response.code(), "Generic Error");
					}
				}
			}

			@Override
			public void onFailure(Call<TMDBResponse> call, Throwable t) {
				listener.onMoviesFetched(false, null, -1, t.getLocalizedMessage());
			}
		});
	}

	public void search(String query, Boolean includeAdult, final iWebServer listener) {
		Call<TMDBResponse> response = iMovieService.searchMovies(
				VERSION,
				API_KEY,
				LANGUAGE,
				query,
				PAGE,
				includeAdult,
				REGION
		);

		response.enqueue(new Callback<TMDBResponse>() {
			@Override
			public void onResponse(Call<TMDBResponse> call, Response<TMDBResponse> response) {
				if (response.code() == 200) {
					listener.onMoviesFetched(true, response.body(), -1, null);
				} else {
					try {
						listener.onMoviesFetched(true, null, response.code(), response.errorBody().string());
					} catch (IOException e) {
						listener.onMoviesFetched(true, null, response.code(), "Generic Error");
					}
				}
			}

			@Override
			public void onFailure(Call<TMDBResponse> call, Throwable t) {
				listener.onMoviesFetched(false, null, -1, t.getLocalizedMessage());
			}
		});
	}
}
