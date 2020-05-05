package it.alessandromarchi.movieapp.models;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String description;

    @SerializedName("poster_path")
    private String imagePath;

    private int isWishlist;

    public Movie(String title, String description, String imagePath, int isWishlist) {
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.isWishlist = isWishlist;
    }

	// GETTER
	public String getTitle() {
		return title;
	}

	// SETTER
	public void setTitle(String title) {
		this.title = title;
	}

    public String getDescription() {
        return description;
    }

	public String getImagePath() {
		return imagePath;
	}

	public int getIsWishlist() {
		return isWishlist;
	}
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public void setIsWishlist(int isWishlist) {
        this.isWishlist = isWishlist;
    }
}
