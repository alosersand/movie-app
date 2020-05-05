package it.alessandromarchi.movieapp.services;

import java.io.IOException;

import it.alessandromarchi.movieapp.models.TMDBResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

	private static WebService instance;

	private String MOVIES_BASE_URL = "https://api.themoviedb.org/";
	private String API_KEY = "5771171ef3fdb433ef45cd105f4b541f";
	private String LANGUAGE = "it-IT";
	private String PAGE = "1";
	private String REGION = "IT";
	private String VERSION = "3";
	private String CATEGORY = "popular";
	private iMovieService iMovieService;

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

	public void getMovies(final iWebServer listener) {
		Call<TMDBResponse> response = iMovieService.getResponse(VERSION, CATEGORY, API_KEY, LANGUAGE, PAGE, REGION);

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
