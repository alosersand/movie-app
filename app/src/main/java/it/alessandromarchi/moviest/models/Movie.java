package it.alessandromarchi.moviest.models;

import com.google.gson.annotations.SerializedName;

public class Movie {
	@SerializedName("title")
	private String title;
	@SerializedName("overview")
	private String description;
	@SerializedName("poster_path")
	private String imagePath;
	@SerializedName("backdrop_path")
	private String backgroundPath;
	@SerializedName("vote_average")
	private float rating;

	public Movie(String title, String description, String imagePath, String backgroundPath, float rating) {
		this.title = title;
		this.description = description;
		this.imagePath = imagePath;
		this.backgroundPath = backgroundPath;
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public String getImagePath() {
		return imagePath;
	}
	public String getBackgroundPath() {
		return backgroundPath;
	}
	public float getRating() {
		return rating;
	}
}
