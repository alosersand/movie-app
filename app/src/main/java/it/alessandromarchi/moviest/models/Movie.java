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

	private int isWishlist;

	@SerializedName("vote_average")
	private float rating;


	public Movie(String title, String description, String imagePath, String backgroundPath, int isWishlist, float rating) {
		this.title = title;
		this.description = description;
		this.imagePath = imagePath;
		this.backgroundPath = backgroundPath;
		this.isWishlist = isWishlist;
		this.rating = rating;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIsWishlist() {
		return isWishlist;
	}

	public String getBackgroundPath() {
		return backgroundPath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setIsWishlist(int isWishlist) {
		this.isWishlist = isWishlist;
	}

	public void setBackgroundPath(String backgroundPath) {
		this.backgroundPath = backgroundPath;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

}
