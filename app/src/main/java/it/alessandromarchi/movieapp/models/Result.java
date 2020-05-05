package it.alessandromarchi.movieapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

	@SerializedName("page")
	private Integer page;
	@SerializedName("total_results")
	private Integer totalResults;
	@SerializedName("total_pages")
	private Integer totalPages;
	@SerializedName("results")
	private List<Movie> movies;

	public List<Movie> getMovies() {
		return movies;
	}
}
