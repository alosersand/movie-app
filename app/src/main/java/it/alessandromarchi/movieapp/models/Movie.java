package it.alessandromarchi.movieapp.models;

public class Movie {
    private String title;
    private String description;
    private String imagePath;
    private int isWishlist;

    public Movie(String title, String description, String imagePath, int isWishlist) {
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.isWishlist = isWishlist;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getIsWishlist() {
        return isWishlist;
    }

    public void setIsWishlist(int isWishlist) {
        this.isWishlist = isWishlist;
    }
}
