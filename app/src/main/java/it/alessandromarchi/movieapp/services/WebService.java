package it.alessandromarchi.movieapp.services;

import java.io.IOException;
import java.util.List;

import it.alessandromarchi.movieapp.models.Movie;
import it.alessandromarchi.movieapp.models.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

	private static WebService instance;
	private List<Movie> movies;
	private String MOVIES_BASE_URL = "https://api.themoviedb.org/3/";
	private String API_KEY = "5771171ef3fdb433ef45cd105f4b541f";
	private String LANGUAGE = "it-IT";
	private String PAGE = "1";
	private String REGION = "IT";
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
		Call<Result> result = iMovieService.getResult(API_KEY, LANGUAGE, PAGE, REGION);
		List<Movie> movies;

		result.enqueue(new Callback<Result>() {
			@Override
			public void onResponse(Call<Result> call, Response<Result> response) {
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
			public void onFailure(Call<Result> call, Throwable t) {
				listener.onMoviesFetched(false, null, -1, t.getLocalizedMessage());
			}
		});
	}
}
